package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerPresentation;
import com.hanielcota.essentials.modules.homes.menu.presentation.MenuContentSlots;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Material picker submenu. Opened when the player drops (Q) a home in /homes; clicking a material
 * updates that home's icon and reopens /homes. The home being modified is read from {@link
 * HomesActionTarget}, populated by {@link HomeClickHandler} right before this menu opens.
 */
@RequiredArgsConstructor
public final class MaterialPickerMenu implements Menu {

  public static final String ID = "essentials.homes.picker";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final MenuService menus;
  private final HomesActionTarget target;
  private final MaterialPickerPresentation presentation;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var menuSpec = config.value().menu();
    var rows = Math.clamp(menuSpec.pickerRows(), MIN_ROWS, MAX_ROWS);
    var title = ComponentUtils.mini(menuSpec.staticPickerTitle());
    var contentSlots = MenuContentSlots.allRows(rows);

    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    MenuFramework.builder(ID, menusRef)
        .rows(rows)
        .title(title)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, MenuSession session) {
    var menuSpec = config.value().menu();
    var palette = menuSpec.palette();
    var loreTemplate = config.value().messages().pickerItemLore();

    var slots = new ArrayList<SlotDefinition>(palette.size());

    for (var material : palette) {
      var template = presentation.render(material, loreTemplate);
      slots.add(SlotDefinition.of(-1, template, click -> handlePick(click.player(), material)));
    }

    return slots;
  }

  private void handlePick(@NonNull Player player, @NonNull Material material) {
    var uuid = player.getUniqueId();
    var homeName = target.consume(uuid);

    if (homeName == null) {
      menus.open(player, HomesMenu.ID);
      return;
    }

    var messages = config.value().messages();
    var applied = service.setMaterial(uuid, homeName, material);

    var replyText = presentation.reply(messages, homeName, material, applied);
    player.sendMessage(ComponentUtils.mini(replyText));

    menus.open(player, HomesMenu.ID);
  }
}
