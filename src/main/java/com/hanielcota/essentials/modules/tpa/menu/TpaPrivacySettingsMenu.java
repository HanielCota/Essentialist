package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPrivacySettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaPrivacySettingsMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.settings.privacy";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;

  static List<Integer> contentSlots(@NonNull TpaPrivacySettingsMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.receiveTpaSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.receiveTpaHereSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.allowCrossWorldSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.blockedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().privacySettingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(
        PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build());
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().privacySettingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var profile = this.profiles.profile(player.getUniqueId());

    return List.of(
        receiveSlot(settings, rows, profile, TeleportRequestType.TPA),
        receiveSlot(settings, rows, profile, TeleportRequestType.TPAHERE),
        crossWorldSlot(settings, rows, profile),
        blockedSlot(settings, rows),
        backSlot(settings, rows));
  }

  private SlotDefinition receiveSlot(
      @NonNull TpaPrivacySettingsMenuConfig settings,
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
    var template = MenuTemplates.simple(material, name, lore);

    var configuredSlot =
        type == TeleportRequestType.TPA ? settings.receiveTpaSlot() : settings.receiveTpaHereSlot();
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> toggleReceive(click, type));
  }

  private SlotDefinition crossWorldSlot(
      @NonNull TpaPrivacySettingsMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var enabled = profile.allowCrossWorld();
    var state = enabled ? settings.enabledLabel() : settings.disabledLabel();
    var material = enabled ? settings.enabledIcon() : settings.disabledIcon();

    var name = settings.allowCrossWorldName().replace("{state}", state);
    var lore = applyState(settings.allowCrossWorldLore(), state);
    var template = MenuTemplates.simple(material, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.allowCrossWorldSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::toggleCrossWorld);
  }

  private SlotDefinition blockedSlot(@NonNull TpaPrivacySettingsMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(
            settings.blockedIcon(), settings.blockedName(), settings.blockedLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.blockedSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaBlockedMenu.ID));
  }

  private SlotDefinition backSlot(@NonNull TpaPrivacySettingsMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaSettingsMenu.ID));
  }

  private void toggleReceive(@NonNull ClickContext click, @NonNull TeleportRequestType type) {
    var playerId = click.player().getUniqueId();
    this.profiles.toggle(playerId, type);
    click.session().refresh();
  }

  private void toggleCrossWorld(@NonNull ClickContext click) {
    var playerId = click.player().getUniqueId();
    this.profiles.toggleAllowCrossWorld(playerId);
    click.session().refresh();
  }

  private static List<String> applyState(@NonNull List<String> lore, @NonNull String state) {
    var replaced = new ArrayList<String>(lore.size());
    for (var line : lore) {
      replaced.add(line.replace("{state}", state));
    }
    return replaced;
  }
}
