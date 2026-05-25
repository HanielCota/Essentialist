package com.hanielcota.essentials.modules.list.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.list.config.GroupDefinition;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.model.PlayerEntry;
import com.hanielcota.essentials.modules.list.model.Resolved;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Builds the visible player roster for /list. Resolves each player's group by walking the
 * configured groups in descending priority and matching against permission nodes; players that
 * don't match anything fall into the default group.
 *
 * <p>Vanish visibility is checked via lazy {@link VanishService} lookup so module load order does
 * not matter — when vanish is disabled every player is visible.
 */
@RequiredArgsConstructor
public final class ListService {

  private final ConfigHandle<ListConfig> config;
  private final PlayerProvider players;
  private final Supplier<Optional<VanishService>> vanishService;

  private static boolean isVisibleTo(
      @NonNull Player player, @Nullable VanishService vanish, boolean seeVanish) {
    if (vanish == null) {
      return true;
    }
    if (seeVanish) {
      return true;
    }

    var id = player.getUniqueId();

    return !vanish.isVanished(id);
  }

  private static List<GroupDefinition> sortedGroups(@NonNull ListConfig snap) {
    var groups = new ArrayList<>(snap.groups());
    groups.sort(Comparator.comparingInt(GroupDefinition::priority).reversed());

    return List.copyOf(groups);
  }

  private static Resolved defaultResolved(@NonNull ListConfig snap) {
    var fallback = snap.defaultGroup();
    var name = fallback.name();
    var material = fallback.material();

    return new Resolved(Resolved.DEFAULT_ID, name, material, Resolved.DEFAULT_PRIORITY);
  }

  private static Resolved resolveGroup(
      @NonNull Player player,
      @NonNull List<GroupDefinition> sortedGroups,
      @NonNull Resolved fallback) {
    for (var group : sortedGroups) {
      if (player.hasPermission(group.permission())) {
        return new Resolved(group.id(), group.name(), group.material(), group.priority());
      }
    }

    return fallback;
  }

  /** Visible roster sorted by group priority desc, then name asc. */
  public List<PlayerEntry> roster(@NonNull Player viewer) {
    var snap = this.config.value();
    var vanish = this.vanishService.get().orElse(null);
    var seeVanish = viewer.hasPermission(VanishVisibilityApplier.SEE_PERMISSION);

    var sortedGroups = sortedGroups(snap);
    var defaultGroup = defaultResolved(snap);
    var entries = new ArrayList<PlayerEntry>();

    for (var player : this.players.all()) {
      if (!isVisibleTo(player, vanish, seeVanish)) {
        continue;
      }

      var group = resolveGroup(player, sortedGroups, defaultGroup);
      var entry = PlayerEntry.of(player.getUniqueId(), player.getName(), group);

      entries.add(entry);
    }

    entries.sort(
        Comparator.comparingInt(PlayerEntry::groupPriority)
            .reversed()
            .thenComparing(PlayerEntry::name, String.CASE_INSENSITIVE_ORDER));

    return entries;
  }

  /** Group counts keyed by group id, used to fill {@code {count_<id>}} in the info template. */
  public Map<String, Integer> countsByGroupId(@NonNull List<PlayerEntry> roster) {
    var counts = new HashMap<String, Integer>();

    for (var entry : roster) {
      counts.merge(entry.groupId(), 1, Integer::sum);
    }

    return Map.copyOf(counts);
  }
}
