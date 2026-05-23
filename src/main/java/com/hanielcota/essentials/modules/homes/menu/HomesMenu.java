package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.menu.presentation.MenuContentSlots;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class HomesMenu implements Menu {

  public static final String ID = "essentials.homes";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeEntryRenderer renderer;
  private final HomeClickHandler clickHandler;

  // Snapshot pushed by /homes so the first render reuses the list already queried by the command.
  private final Map<UUID, List<Home>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<Home> entries) {
    prefetched.put(viewer, List.copyOf(entries));
  }

  public void clearPrefetched(@NonNull UUID viewer) {
    prefetched.remove(viewer);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var menuSpec = config.value().menu();
    var rows = Math.clamp(menuSpec.rows(), MIN_ROWS, MAX_ROWS);
    var title = ComponentUtils.mini(menuSpec.title());
    var contentSlots = MenuContentSlots.allRows(rows);

    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(title)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var uuid = player.getUniqueId();
    var entries = prefetched.remove(uuid);

    if (entries == null) {
      entries = service.list(uuid);
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());

    for (var i = 0; i < entries.size(); i++) {
      var home = entries.get(i);
      var humanIndex = i + 1;
      var template = renderer.render(home, humanIndex);

      slots.add(SlotDefinition.of(-1, template, click -> clickHandler.handle(click, home)));
    }

    return slots;
  }
}
