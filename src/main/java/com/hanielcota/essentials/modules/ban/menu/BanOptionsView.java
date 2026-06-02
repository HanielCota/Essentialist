package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.config.BanMenuConfig;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Lays out the ban-options grid for one viewer: the selected target's head, a row of duration
 * buttons, a block of reason buttons and the back/confirm controls. Reads the in-progress {@link
 * BanSelection} so the chosen duration and reason render with the "selected" marker.
 */
@RequiredArgsConstructor
public final class BanOptionsView {

  private static final int TARGET_SLOT = 4;
  private static final int DURATION_START = 9;
  private static final int REASON_START = 18;

  private final ConfigHandle<BanConfig> config;
  private final BanMenuState state;
  private final BanOptionsClickHandler clicks;

  public List<SlotDefinition> slotsFor(@NonNull Player viewer, @NonNull MenuSession session) {
    var viewerId = viewer.getUniqueId();
    var selection = this.state.get(viewerId);

    if (selection == null) {
      return List.of();
    }

    var snap = this.config.value();
    var menu = snap.menu();
    var rows = MenuLayouts.clampRows(menu.optionsRows());

    var slots = new ArrayList<SlotDefinition>();
    slots.add(targetSlot(menu, selection));
    addDurationSlots(slots, snap, menu, selection);
    addReasonSlots(slots, snap, menu, selection, rows);
    slots.add(backSlot(menu, rows));
    slots.add(confirmSlot(snap, menu, selection, rows));

    return slots;
  }

  private SlotDefinition targetSlot(@NonNull BanMenuConfig menu, @NonNull BanSelection selection) {
    var targetName = selection.targetName();
    var name = menu.targetName().replace("{player}", targetName);

    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    builder.name(name);
    builder.lore(menu.targetLore().toArray(String[]::new));
    builder.italic(false);
    MenuTemplates.applyHead(builder, Material.PLAYER_HEAD, true, "", selection.targetId());

    return SlotDefinition.of(TARGET_SLOT, builder.build(), click -> {});
  }

  private void addDurationSlots(
      @NonNull List<SlotDefinition> slots,
      @NonNull BanConfig snap,
      @NonNull BanMenuConfig menu,
      @NonNull BanSelection selection) {
    var durations = snap.durations();

    for (var i = 0; i < durations.size(); i++) {
      var slot = DURATION_START + i;
      if (slot >= REASON_START) {
        break;
      }

      var option = durations.get(i);
      var selected = option.duration().equals(selection.durationRaw());
      var template = optionTemplate(option.icon(), option.name(), List.of(), selected, menu);

      var raw = option.duration();
      var label = option.name();
      slots.add(
          SlotDefinition.of(slot, template, click -> this.clicks.setDuration(click, raw, label)));
    }
  }

  private void addReasonSlots(
      @NonNull List<SlotDefinition> slots,
      @NonNull BanConfig snap,
      @NonNull BanMenuConfig menu,
      @NonNull BanSelection selection,
      int rows) {
    var reasons = snap.reasons();
    var backSlot = (rows - 1) * 9;

    for (var i = 0; i < reasons.size(); i++) {
      var slot = REASON_START + i;
      if (slot >= backSlot) {
        break;
      }

      var option = reasons.get(i);
      var selected = option.reason().equals(selection.reason());
      var template = optionTemplate(option.icon(), option.name(), option.lore(), selected, menu);

      var reason = option.reason();
      slots.add(SlotDefinition.of(slot, template, click -> this.clicks.setReason(click, reason)));
    }
  }

  private SlotDefinition backSlot(@NonNull BanMenuConfig menu, int rows) {
    var slot = (rows - 1) * 9;
    var template = MenuTemplates.simple(Material.ARROW, menu.backName(), List.of());

    return SlotDefinition.of(slot, template, this.clicks::back);
  }

  private SlotDefinition confirmSlot(
      @NonNull BanConfig snap,
      @NonNull BanMenuConfig menu,
      @NonNull BanSelection selection,
      int rows) {
    var slot = (rows - 1) * 9 + 4;

    var durationLabel = selection.durationLabel();
    var reason = selection.reason();
    var reasonLabel = reason == null ? "—" : reason;
    var targetName = selection.targetName();

    var lore = new ArrayList<String>(menu.confirmLore().size());
    for (var line : menu.confirmLore()) {
      var formatted =
          Placeholders.format(
              line, "player", targetName, "duration", durationLabel, "reason", reasonLabel);
      lore.add(formatted);
    }

    var template = MenuTemplates.simple(Material.LIME_DYE, menu.confirmName(), lore);

    return SlotDefinition.of(slot, template, this.clicks::confirm);
  }

  private static ItemTemplate optionTemplate(
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> baseLore,
      boolean selected,
      @NonNull BanMenuConfig menu) {
    var lore = new ArrayList<String>(baseLore);
    if (selected) {
      lore.add(menu.selectedMarker());
    }

    return MenuTemplates.simple(icon, name, lore);
  }
}
