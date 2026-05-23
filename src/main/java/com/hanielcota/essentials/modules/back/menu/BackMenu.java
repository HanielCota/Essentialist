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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

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

  public BackMenu(
      ConfigHandle<BackConfig> config,
      TeleportHistory history,
      BackEntryRenderer renderer,
      BackClickHandler clickHandler) {
    this.config = Objects.requireNonNull(config, "config");
    this.history = Objects.requireNonNull(history, "history");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  /**
   * Filters configured slot indices to those that fit inside a menu of {@code capacity} slots,
   * dropping nulls, negatives, out-of-range and duplicate values. Falls back to the leading slots
   * when nothing usable remains, since {@link PaginationConfig} rejects an empty content-slot list.
   */
  private static List<Integer> sanitizeContentSlots(List<Integer> configured, int capacity) {
    var valid =
        configured.stream()
            .filter(Objects::nonNull)
            .filter(slot -> slot >= 0 && slot < capacity)
            .distinct()
            .toList();
    if (!valid.isEmpty()) {
      return valid;
    }
    int fallback = Math.min(capacity, TeleportHistory.CAPACITY);
    return IntStream.range(0, fallback).boxed().toList();
  }

  /** Stores a history snapshot to be consumed by this viewer's next menu render. */
  public void prefetch(@NonNull UUID viewer, @NonNull List<HistoryEntry> entries) {
    Objects.requireNonNull(viewer, "viewer");
    Objects.requireNonNull(entries, "entries");
    prefetched.put(viewer, List.copyOf(entries));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var snap = config.value();
    int rows = Math.max(MIN_ROWS, Math.min(MAX_ROWS, snap.menuRows()));
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

    for (int i = 0; i < entries.size(); i++) {
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
