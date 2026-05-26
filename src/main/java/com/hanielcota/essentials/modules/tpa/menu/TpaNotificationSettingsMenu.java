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
import com.hanielcota.essentials.modules.tpa.config.TpaNotificationSettingsMenuConfig;
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
public final class TpaNotificationSettingsMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.settings.notifications";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;

  static List<Integer> contentSlots(@NonNull TpaNotificationSettingsMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.soundsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.notifyWhenFavoritedSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().notificationSettingsMenu();
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
    var settings = this.config.value().notificationSettingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var profile = this.profiles.profile(player.getUniqueId());

    return List.of(
        soundsSlot(settings, rows, profile),
        notifyWhenFavoritedSlot(settings, rows, profile),
        backSlot(settings, rows));
  }

  private SlotDefinition soundsSlot(
      @NonNull TpaNotificationSettingsMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var enabled = profile.soundsEnabled();
    var state = enabled ? settings.enabledLabel() : settings.disabledLabel();
    var material = enabled ? settings.enabledIcon() : settings.disabledIcon();

    var name = settings.soundsName().replace("{state}", state);
    var lore = applyState(settings.soundsLore(), state);
    var template = simpleTemplate(material, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.soundsSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::toggleSounds);
  }

  private SlotDefinition notifyWhenFavoritedSlot(
      @NonNull TpaNotificationSettingsMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var enabled = profile.notifyWhenFavorited();
    var state = enabled ? settings.enabledLabel() : settings.disabledLabel();
    var material = enabled ? settings.enabledIcon() : settings.disabledIcon();

    var name = settings.notifyWhenFavoritedName().replace("{state}", state);
    var lore = applyState(settings.notifyWhenFavoritedLore(), state);
    var template = simpleTemplate(material, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.notifyWhenFavoritedSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::toggleNotifyWhenFavorited);
  }

  private SlotDefinition backSlot(@NonNull TpaNotificationSettingsMenuConfig settings, int rows) {
    var template = simpleTemplate(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaSettingsMenu.ID));
  }

  private void toggleSounds(@NonNull ClickContext click) {
    this.profiles.toggleSounds(click.player().getUniqueId());
    click.session().refresh();
  }

  private void toggleNotifyWhenFavorited(@NonNull ClickContext click) {
    this.profiles.toggleNotifyWhenFavorited(click.player().getUniqueId());
    click.session().refresh();
  }

  private static ItemTemplate simpleTemplate(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
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
}
