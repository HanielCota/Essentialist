package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaSettingsMenuConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Settings hub — navigation-only screen. Routes the viewer to one of three category sub-menus
 * (Privacy / Notifications / Behavior), shows the read-only cooldown info and links back to the
 * /tpa hub.
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
    var settings = this.config.value().settingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    return List.of(
        categorySlot(
            settings.privacySlot(),
            rows,
            settings.privacyIcon(),
            settings.privacyName(),
            settings.privacyLore(),
            TpaPrivacySettingsMenu.ID),
        categorySlot(
            settings.notificationsSlot(),
            rows,
            settings.notificationsIcon(),
            settings.notificationsName(),
            settings.notificationsLore(),
            TpaNotificationSettingsMenu.ID),
        categorySlot(
            settings.behaviorSlot(),
            rows,
            settings.behaviorIcon(),
            settings.behaviorName(),
            settings.behaviorLore(),
            TpaBehaviorSettingsMenu.ID),
        cooldownSlot(settings, rows),
        backSlot(settings, rows));
  }

  private SlotDefinition categorySlot(
      int configuredSlot,
      int rows,
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> lore,
      @NonNull String targetMenuId) {
    var template = simpleTemplate(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(targetMenuId));
  }

  private SlotDefinition cooldownSlot(@NonNull TpaSettingsMenuConfig settings, int rows) {
    var template =
        simpleTemplate(settings.cooldownIcon(), settings.cooldownName(), settings.cooldownLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.cooldownSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition backSlot(@NonNull TpaSettingsMenuConfig settings, int rows) {
    var template = simpleTemplate(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }
}
