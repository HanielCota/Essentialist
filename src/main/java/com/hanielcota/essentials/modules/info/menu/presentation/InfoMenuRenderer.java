package com.hanielcota.essentials.modules.info.menu.presentation;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.menu.InfoTab;
import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class InfoMenuRenderer {

  private static final Log LOG = Log.of(InfoMenuRenderer.class);

  private final ConfigHandle<InfoConfig> config;
  private final ServerInfoEntries serverEntries;
  private final PlayerInfoEntries playerEntries;
  private final PluginInfoEntries pluginEntries;

  public List<SlotDefinition> slots(
      @NonNull Player target,
      @NonNull InfoTab tab,
      @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    return switch (tab) {
      case CATEGORIES -> categorySlots(tabSwitcher);
      case SERVER -> serverSlots(tabSwitcher);
      case PLAYER -> playerSlots(target, tabSwitcher);
      case ABOUT -> aboutSlots(tabSwitcher);
    };
  }

  private List<SlotDefinition> serverSlots(@NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var entries = this.serverEntries.entries();

    return detailSlots(entries, tabSwitcher);
  }

  private List<SlotDefinition> aboutSlots(@NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var entries = this.pluginEntries.entries();

    return detailSlots(entries, tabSwitcher);
  }

  private List<SlotDefinition> playerSlots(
      @NonNull Player viewer, @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var entries = this.playerEntries.entries(viewer);

    return detailSlots(entries, tabSwitcher);
  }

  private List<SlotDefinition> categorySlots(
      @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var snap = this.config.value();

    var serverSlot =
        InfoSlotFactory.category(
            snap.effectiveServerSlot(),
            snap.serverMaterial(),
            snap.serverName(),
            snap.serverLore(),
            InfoTab.SERVER,
            tabSwitcher);

    var playerSlot =
        InfoSlotFactory.category(
            snap.effectivePlayerSlot(),
            snap.playerMaterial(),
            snap.playerName(),
            snap.playerLore(),
            InfoTab.PLAYER,
            tabSwitcher);

    var aboutSlot =
        InfoSlotFactory.category(
            snap.effectiveAboutSlot(),
            snap.aboutMaterial(),
            snap.aboutName(),
            snap.aboutLore(),
            InfoTab.ABOUT,
            tabSwitcher);

    return List.of(serverSlot, playerSlot, aboutSlot);
  }

  private List<SlotDefinition> detailSlots(
      @NonNull List<InfoEntry> entries, @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var snap = this.config.value();
    var detailSlots = snap.effectiveDetailSlots();
    var entryCount = entries.size();
    var slotCount = detailSlots.size();
    var visibleEntries = Math.min(entryCount, slotCount);

    if (entryCount > slotCount) {
      var dropped = entryCount - slotCount;
      LOG.warn(
          "Info menu has {} entries but only {} detailSlots configured — {} entries hidden.",
          entryCount,
          slotCount,
          dropped);
    }

    var startIdx = (slotCount - visibleEntries) / 2;

    var slots = new ArrayList<SlotDefinition>(visibleEntries + 1);

    for (var i = 0; i < visibleEntries; i++) {
      var targetSlot = detailSlots.get(startIdx + i);
      var entry = entries.get(i);
      var slotDef = InfoSlotFactory.entryItem(targetSlot, entry);

      slots.add(slotDef);
    }

    var backDef = InfoSlotFactory.back(snap, tabSwitcher);
    slots.add(backDef);

    return slots;
  }
}
