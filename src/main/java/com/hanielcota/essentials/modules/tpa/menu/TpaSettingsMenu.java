package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaSettingsMenuConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Settings hub — navigation-only screen. Routes the viewer to one of three category sub-menus
 * (Privacy / Notifications / Behavior), shows the read-only cooldown info and links back to the
 * /tpa hub. Every item is purely config-driven with no player-specific data, so slots are defined
 * statically at registration time.
 */
@RequiredArgsConstructor
public final class TpaSettingsMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.settings";

  private final ConfigHandle<TpaConfig> config;

  static List<Integer> contentSlots(@NonNull TpaSettingsMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.privacySlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.notificationsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.behaviorSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.cooldownSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().settingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var privacyTemplate =
        MenuTemplates.simple(
            settings.privacyIcon(), settings.privacyName(), settings.privacyLore());
    var privacySlot = MenuLayouts.sanitizeSlot(settings.privacySlot(), rows, 0);

    var notificationsTemplate =
        MenuTemplates.simple(
            settings.notificationsIcon(),
            settings.notificationsName(),
            settings.notificationsLore());
    var notificationsSlot = MenuLayouts.sanitizeSlot(settings.notificationsSlot(), rows, 0);

    var behaviorTemplate =
        MenuTemplates.simple(
            settings.behaviorIcon(), settings.behaviorName(), settings.behaviorLore());
    var behaviorSlot = MenuLayouts.sanitizeSlot(settings.behaviorSlot(), rows, 0);

    var cooldownTemplate =
        MenuTemplates.simple(
            settings.cooldownIcon(), settings.cooldownName(), settings.cooldownLore());
    var cooldownSlot = MenuLayouts.sanitizeSlot(settings.cooldownSlot(), rows, 0);

    var backTemplate =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var backSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.slot(privacySlot, privacyTemplate, click -> click.switchTo(TpaPrivacySettingsMenu.ID));
    builder.slot(
        notificationsSlot,
        notificationsTemplate,
        click -> click.switchTo(TpaNotificationSettingsMenu.ID));
    builder.slot(
        behaviorSlot, behaviorTemplate, click -> click.switchTo(TpaBehaviorSettingsMenu.ID));
    builder.slot(cooldownSlot, cooldownTemplate, click -> {});
    builder.slot(backSlot, backTemplate, click -> click.switchTo(TpaHelpMenu.ID));

    var menu = builder.build();
    menu.register();
  }
}
