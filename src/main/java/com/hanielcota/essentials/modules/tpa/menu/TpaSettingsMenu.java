package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaSettingsMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.settings";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;

  static List<Integer> contentSlots(@NonNull TpaSettingsMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.receiveTpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.receiveTpaHereSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.blockedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  private static ItemTemplate template(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }

  private static List<String> applyState(@NonNull List<String> lore, @NonNull String state) {
    var replaced = new ArrayList<String>(lore.size());

    for (var line : lore) {
      replaced.add(line.replace("{state}", state));
    }

    return replaced;
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var settings = snap.settingsMenu();

    var rows = MenuLayouts.clampRows(settings.rows());
    var rawTitle = settings.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(
        PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build());
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var settings = snap.settingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var playerId = player.getUniqueId();
    var profile = this.profiles.profile(playerId);

    return List.of(
        toggleSlot(settings, rows, profile, TeleportRequestType.TPA),
        toggleSlot(settings, rows, profile, TeleportRequestType.TPAHERE),
        blockedSlot(settings, rows),
        backSlot(settings, rows));
  }

  private SlotDefinition toggleSlot(
      @NonNull TpaSettingsMenuConfig settings,
      int rows,
      @NonNull TpaProfile profile,
      @NonNull TeleportRequestType type) {
    var enabled = profile.accepts(type);
    var state = enabled ? settings.enabledLabel() : settings.disabledLabel();
    var material = enabled ? settings.enabledIcon() : settings.disabledIcon();

    var nameTemplate =
        type == TeleportRequestType.TPA ? settings.receiveTpaName() : settings.receiveTpaHereName();
    var loreTemplate =
        type == TeleportRequestType.TPA ? settings.receiveTpaLore() : settings.receiveTpaHereLore();

    var name = nameTemplate.replace("{state}", state);
    var lore = applyState(loreTemplate, state);
    var template = template(material, name, lore);

    var configuredSlot =
        type == TeleportRequestType.TPA ? settings.receiveTpaSlot() : settings.receiveTpaHereSlot();
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> toggle(click, type));
  }

  private SlotDefinition backSlot(@NonNull TpaSettingsMenuConfig settings, int rows) {
    var template = template(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }

  private SlotDefinition blockedSlot(@NonNull TpaSettingsMenuConfig settings, int rows) {
    var template = template(settings.blockedIcon(), settings.blockedName(), settings.blockedLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.blockedSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaBlockedMenu.ID));
  }

  private void toggle(@NonNull ClickContext click, @NonNull TeleportRequestType type) {
    var player = click.player();
    var playerId = player.getUniqueId();

    this.profiles.toggle(playerId, type);
    click.session().refresh();
  }
}
