package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.Home;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
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
public final class HomesMenu implements Menu, Listener {

  public static final String ID = "essentials.homes";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeEntryRenderer renderer;
  private final HomeClickHandler clickHandler;

  // Snapshot pushed by /homes so the first render reuses the list already queried by the command.
  private final Map<UUID, List<Home>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<Home> entries) {
    prefetched.put(viewer, List.copyOf(entries));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var menu = config.value().menu();
    var rows = Math.clamp(menu.rows(), MIN_ROWS, MAX_ROWS);

    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(ComponentUtils.mini(menu.title()))
        .pagination(PaginationConfig.builder().contentSlots(contentSlots(rows)).build())
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private static List<Integer> contentSlots(int rows) {
    var capacity = rows * SLOTS_PER_ROW;
    var slots = new ArrayList<Integer>(capacity);

    for (var i = 0; i < capacity; i++) slots.add(i);
    return slots;
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var entries = prefetched.remove(player.getUniqueId());
    if (entries == null) {
      entries = service.list(player.getUniqueId());
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var i = 0; i < entries.size(); i++) {
      var home = entries.get(i);
      var template = renderer.render(home, i + 1);
      slots.add(SlotDefinition.of(-1, template, click -> clickHandler.handle(click, home)));
    }
    return slots;
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    prefetched.remove(event.getPlayer().getUniqueId());
  }
}
