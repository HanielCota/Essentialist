package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Material picker submenu. Opened when the player drops (Q) a home in /homes; clicking a material
 * updates that home's icon and reopens /homes. The home name is read from {@link
 * HomesActionTarget}, which {@link HomeClickHandler} set just before opening this menu.
 */
@RequiredArgsConstructor
public final class MaterialPickerMenu implements Menu {

  public static final String ID = "essentials.homes.picker";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final MenuService menus;
  private final HomesActionTarget target;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var menu = config.value().menu();
    var rows = Math.clamp(menu.pickerRows(), MIN_ROWS, MAX_ROWS);

    MenuFramework.builder(ID, menusRef)
        .rows(rows)
        .title(ComponentUtils.mini(menu.pickerTitle().replace("{name}", "?")))
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
    var menu = config.value().menu();
    var palette = menu.palette();
    var slots = new ArrayList<SlotDefinition>(palette.size());

    for (var i = 0; i < palette.size(); i++) {
      var material = palette.get(i);
      var template = renderMaterial(material);
      slots.add(SlotDefinition.of(-1, template, click -> handlePick(click.player(), material)));
    }
    return slots;
  }

  private ItemTemplate renderMaterial(Material material) {
    var loreLine =
        config.value().messages().pickerItemLore().replace("{material}", prettyName(material));
    return ItemTemplate.builder(material)
        .name("<gold>" + prettyName(material))
        .lore(new String[] {loreLine})
        .italic(false)
        .build();
  }

  private void handlePick(Player player, Material material) {
    var homeName = target.consume(player.getUniqueId()).orElse(null);
    var messages = config.value().messages();

    if (homeName == null) {
      menus.open(player, HomesMenu.ID);
      return;
    }

    if (service.setMaterial(player.getUniqueId(), homeName, material)) {
      player.sendMessage(
          ComponentUtils.mini(
              messages
                  .materialUpdated()
                  .replace("{name}", homeName)
                  .replace("{material}", prettyName(material))));
    } else {
      player.sendMessage(ComponentUtils.mini(messages.unknownHome().replace("{name}", homeName)));
    }

    menus.open(player, HomesMenu.ID);
  }

  private static String prettyName(Material material) {
    return material.name().toLowerCase().replace('_', ' ');
  }
}
