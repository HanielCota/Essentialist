package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class BackMenu implements Menu, Listener {

  public static final String ID = "essentials.back";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<BackConfig> config;
  private final TeleportHistory history;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;

  /**
   * One-shot history snapshots handed over by {@link
   * com.hanielcota.essentials.modules.back.command.BackCommand} so the menu's first render reuses
   * the list the command already queried instead of hitting the database again. Consumed (removed)
   * on the first {@link #buildSlots} call; later refreshes/pagination re-query for fresh data.
   */
  private final Map<UUID, List<HistoryEntry>> prefetched = new ConcurrentHashMap<>();

  // Filters configured slot indices to fit inside `capacity`, dropping
  // nulls/out-of-range/duplicates.
  // Falls back to leading slots when nothing usable remains — PaginationConfig rejects empty lists.
  private static List<Integer> sanitizeContentSlots(List<Integer> configured, int capacity) {
    var valid = new ArrayList<Integer>(configured.size());
    var seen = new HashSet<Integer>(configured.size());

    for (var slot : configured) {
      if (slot == null || slot < 0 || slot >= capacity) continue;
      if (seen.add(slot)) valid.add(slot);
    }
    if (!valid.isEmpty()) {
      return valid;
    }

    var fallback = Math.min(capacity, TeleportHistory.CAPACITY);
    var leading = new ArrayList<Integer>(fallback);
    for (var i = 0; i < fallback; i++) leading.add(i);
    return leading;
  }

  public void prefetch(@NonNull UUID viewer, @NonNull List<HistoryEntry> entries) {
    prefetched.put(viewer, List.copyOf(entries));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = config.value();
    var rows = Math.max(MIN_ROWS, Math.min(MAX_ROWS, snap.menuRows()));
    var slots = sanitizeContentSlots(snap.menuContentSlots(), rows * SLOTS_PER_ROW);
    var pagination = PaginationConfig.builder().contentSlots(slots).build();

    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(ComponentUtils.mini(snap.menuTitle()))
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var entries = prefetched.remove(player.getUniqueId());
    if (entries == null) {
      entries = history.list(player.getUniqueId());
    }
    var slots = new ArrayList<SlotDefinition>(entries.size());

    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var template = renderer.render(entry, i + 1);
      slots.add(SlotDefinition.of(-1, template, click -> clickHandler.handle(click, entry)));
    }

    return slots;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    prefetched.remove(event.getPlayer().getUniqueId());
  }
}
