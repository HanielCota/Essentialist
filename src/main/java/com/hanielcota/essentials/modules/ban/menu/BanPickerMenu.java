package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Landing menu of {@code /ban}: a "search by name" card followed by every online player's head. */
@RequiredArgsConstructor
public final class BanPickerMenu implements EssentialsMenu {

  public static final String ID = "essentials.ban.picker";

  private final ConfigHandle<BanConfig> config;
  private final BanPickerClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = MenuLayouts.clampRows(menu.pickerRows());
    var title = menu.pickerTitle();

    var lastRow = (rows - 1) * 9;
    var contentSlots = MenuLayouts.fallbackContentSlots(rows, lastRow);
    var navigation = NavigationButtonsConfig.defaults(lastRow + 3, lastRow + 5);
    var infoSlot = lastRow + 8;
    var infoTemplate = MenuTemplates.info(Material.PAPER, title, List.of());

    PaginatedInfoMenus.register(
        menus, ID, rows, title, contentSlots, navigation, infoSlot, infoTemplate, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player viewer, @NonNull MenuSession session) {
    var snap = this.config.value();
    var menu = snap.menu();

    var slots = new ArrayList<SlotDefinition>();

    var searchTemplate =
        MenuTemplates.simple(Material.NAME_TAG, menu.searchName(), menu.searchLore());
    slots.add(SlotDefinition.of(-1, searchTemplate, this.clicks::promptNick));

    for (var online : sortedOnline()) {
      var targetId = online.getUniqueId();
      var targetName = online.getName();
      var template = headTemplate(targetName, targetId);

      slots.add(
          SlotDefinition.of(
              -1, template, click -> this.clicks.selectOnline(click, targetId, targetName)));
    }

    return slots;
  }

  private static List<Player> sortedOnline() {
    var online = new ArrayList<Player>(Bukkit.getOnlinePlayers());

    online.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));

    return online;
  }

  private static ItemTemplate headTemplate(@NonNull String name, @NonNull UUID targetId) {
    var displayName = "<white>" + name;

    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    builder.name(displayName);
    builder.italic(false);
    MenuTemplates.applyHead(builder, Material.PLAYER_HEAD, true, "", targetId);

    return builder.build();
  }
}
