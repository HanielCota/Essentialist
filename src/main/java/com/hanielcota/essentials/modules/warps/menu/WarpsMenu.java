package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.config.WarpsMenuConfig;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpFilterPreferences;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Paginated warps list. Wires content (renderer), the filter button (filter renderer) and info. */
@RequiredArgsConstructor
public final class WarpsMenu implements EssentialsMenu {

  public static final String ID = "essentials.warps";

  private static final String COUNT_TOKEN = "{count}";

  private final ConfigHandle<WarpsConfig> config;
  private final WarpService service;
  private final WarpEntryRenderer renderer;
  private final WarpClickHandler clickHandler;
  private final WarpFilterRenderer filterRenderer;
  private final WarpOccupancy occupancy;
  private final WarpLikes likes;
  private final WarpFavorites favorites;
  private final WarpFilterPreferences filters;

  private static ItemTemplate infoTemplate(@NonNull WarpsMenuConfig menu, int warpCount) {
    var countText = Integer.toString(warpCount);
    var lore = menu.infoLore().stream().map(line -> line.replace(COUNT_TOKEN, countText)).toList();

    return MenuTemplates.info(menu.infoMaterial(), menu.infoName(), lore);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var menu = this.config.value().menu();
    var rows = menu.rows();
    var infoSlot = MenuLayouts.sanitizeSlot(menu.infoSlot(), rows, 0);
    var baseInfo = infoTemplate(menu, 0);
    var filler = menu.filler();

    PaginatedInfoMenus.register(
        menus,
        ID,
        rows,
        menu.title(),
        menu.contentSlots(),
        menu.navigation(),
        infoSlot,
        baseInfo,
        this::buildSlots,
        builder -> {
          if (filler != Material.AIR) {
            builder.fillEmpty(filler);
          }
        });
  }

  private void onFilterClicked(@NonNull ClickContext click) {
    var playerId = click.player().getUniqueId();
    this.filters.cycle(playerId);
    click.refresh();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var playerId = player.getUniqueId();
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = menu.rows();

    var accessible = this.service.visibleTo(player);
    var filter = this.filters.of(playerId);
    var data = new WarpMenuData(playerId, snap, this.occupancy, this.likes, this.favorites);
    var shown = WarpFilters.apply(accessible, filter, data);

    var infoSlot = MenuLayouts.sanitizeSlot(menu.infoSlot(), rows, 0);
    var filterSlot = MenuLayouts.sanitizeSlot(menu.filter().slot(), rows, 0);
    var filterItem = this.filterRenderer.render(menu.filter(), filter);

    var slots = new ArrayList<SlotDefinition>(shown.size() + 2);
    slots.add(SlotDefinition.of(infoSlot, infoTemplate(menu, accessible.size()), click -> {}));
    slots.add(SlotDefinition.of(filterSlot, filterItem, this::onFilterClicked));

    for (var warp : shown) {
      var template = this.renderer.render(warp, playerId);
      ClickHandler onClick = click -> this.clickHandler.handle(click, warp);
      slots.add(SlotDefinition.of(-1, template, onClick));
    }

    return slots;
  }
}
