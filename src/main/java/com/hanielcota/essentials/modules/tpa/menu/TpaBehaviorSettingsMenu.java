package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PaginatedMenus;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaBehaviorSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaDndCycle;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaBehaviorSettingsMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.settings.behavior";

  private final ConfigHandle<TpaConfig> config;
  private final TpaProfileService profiles;

  static List<Integer> contentSlots(@NonNull TpaBehaviorSettingsMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.autoAcceptSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.dndSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().behaviorSettingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);

    PaginatedMenus.register(menus, ID, rows, settings.title(), slots, this::buildSlots);
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().behaviorSettingsMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var profile = this.profiles.profile(player.getUniqueId());

    return List.of(
        autoAcceptSlot(settings, rows, profile),
        dndSlot(settings, rows, profile),
        backSlot(settings, rows));
  }

  private SlotDefinition autoAcceptSlot(
      @NonNull TpaBehaviorSettingsMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var enabled = profile.autoAcceptFavorites();
    var state = enabled ? settings.enabledLabel() : settings.disabledLabel();
    var material = enabled ? settings.enabledIcon() : settings.disabledIcon();

    var name = settings.autoAcceptName().replace("{state}", state);
    var lore = applyState(settings.autoAcceptLore(), state);
    var template = MenuTemplates.simple(material, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.autoAcceptSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::toggleAutoAccept);
  }

  private SlotDefinition dndSlot(
      @NonNull TpaBehaviorSettingsMenuConfig settings, int rows, @NonNull TpaProfile profile) {
    var now = System.currentTimeMillis();
    var durations = TpaDndPresenter.durations(settings);
    var stage = TpaDndCycle.stageOf(profile.dndUntilEpochMs(), now, durations);
    var stateLabel = TpaDndPresenter.stageLabel(settings, stage);
    var remainingLabel = TpaDndPresenter.stageRemaining(settings, profile, now);

    var name =
        Placeholders.format(settings.dndName(), "state", stateLabel, "remaining", remainingLabel);
    var lore = TpaDndPresenter.renderLore(settings, stateLabel, remainingLabel, stage);
    var icon = stage == TpaDndCycle.Stage.OFF ? settings.dndOffIcon() : settings.dndOnIcon();
    var template = MenuTemplates.simple(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.dndSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::cycleDnd);
  }

  private SlotDefinition backSlot(@NonNull TpaBehaviorSettingsMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaSettingsMenu.ID));
  }

  private void toggleAutoAccept(@NonNull ClickContext click) {
    this.profiles.toggleAutoAcceptFavorites(click.player().getUniqueId());
    click.session().refresh();
  }

  private void cycleDnd(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    var profile = this.profiles.profile(viewerId);
    var now = System.currentTimeMillis();
    var settings = this.config.value().behaviorSettingsMenu();
    var durations = TpaDndPresenter.durations(settings);

    var currentStage = TpaDndCycle.stageOf(profile.dndUntilEpochMs(), now, durations);
    var nextStage = currentStage.next();
    var nextUntil = TpaDndCycle.cycleTo(nextStage, now, durations);

    this.profiles.setDndUntil(viewerId, nextUntil);
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
