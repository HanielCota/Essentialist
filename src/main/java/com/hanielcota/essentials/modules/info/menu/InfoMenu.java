package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.presentation.InfoEntry;
import com.hanielcota.essentials.modules.info.service.InfoService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Single /info menu with category and detail tabs. Switching tabs re-renders this same inventory
 * via {@link ClickContext#refresh()} — it never opens a separate menu, which keeps the framework's
 * navigation history and session intact.
 */
@RequiredArgsConstructor
public final class InfoMenu implements EssentialsMenu {

  public static final String ID = "essentials.info";

  private final ConfigHandle<InfoConfig> config;
  private final InfoService service;
  private final InfoMenuState state;

  private static SlotDefinition entryItem(int slot, @NonNull InfoEntry entry) {
    var icon = entry.icon();
    var name = entry.name();
    var lore = entry.lore();
    var loreArray = lore.toArray(String[]::new);
    var headOwner = entry.headOwner();

    var builder = ItemTemplate.builder(icon);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.italic(false);

    if (headOwner != null) {
      builder = builder.head(headOwner);
    }

    var template = builder.build();

    return SlotDefinition.of(slot, template, click -> {});
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var contentSlots = snap.effectiveContentSlots();

    var pagBuilder = PaginationConfig.builder();
    pagBuilder = pagBuilder.contentSlots(contentSlots);
    var pagination = pagBuilder.build();

    var rawTitle = snap.menuTitle();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var menuBuilder = MenuFramework.builder(ID, menus);
    menuBuilder = menuBuilder.rows(rows);
    menuBuilder = menuBuilder.title(menuTitle);
    menuBuilder = menuBuilder.pagination(pagination);
    menuBuilder = menuBuilder.dynamicContent(this::buildSlots);

    var menu = menuBuilder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var tab = this.state.tab(viewerId);

    return switch (tab) {
      case CATEGORIES -> categorySlots();
      case SERVER -> serverSlots();
      case PLAYER -> playerSlots(player);
      case ABOUT -> aboutSlots();
    };
  }

  private List<SlotDefinition> serverSlots() {
    var entries = this.service.serverEntries();

    return detailSlots(entries);
  }

  private List<SlotDefinition> aboutSlots() {
    var entries = this.service.aboutEntries();

    return detailSlots(entries);
  }

  private List<SlotDefinition> playerSlots(@NonNull Player viewer) {
    var target = this.state.resolveTarget(viewer);
    var entries = this.service.playerEntries(target);

    return detailSlots(entries);
  }

  private List<SlotDefinition> categorySlots() {
    var snap = this.config.value();

    var serverSlot =
        category(
            snap.effectiveServerSlot(),
            snap.serverMaterial(),
            snap.serverName(),
            snap.serverLore(),
            InfoTab.SERVER);

    var playerSlot =
        category(
            snap.effectivePlayerSlot(),
            snap.playerMaterial(),
            snap.playerName(),
            snap.playerLore(),
            InfoTab.PLAYER);

    var aboutSlot =
        category(
            snap.effectiveAboutSlot(),
            snap.aboutMaterial(),
            snap.aboutName(),
            snap.aboutLore(),
            InfoTab.ABOUT);

    return List.of(serverSlot, playerSlot, aboutSlot);
  }

  private List<SlotDefinition> detailSlots(@NonNull List<InfoEntry> entries) {
    var snap = this.config.value();
    var detailSlots = snap.effectiveDetailSlots();
    var visibleEntries = Math.min(entries.size(), detailSlots.size());
    var startIdx = (detailSlots.size() - visibleEntries) / 2;

    var slots = new ArrayList<SlotDefinition>(visibleEntries + 1);

    for (var i = 0; i < visibleEntries; i++) {
      var targetSlot = detailSlots.get(startIdx + i);
      var entry = entries.get(i);
      var slotDef = entryItem(targetSlot, entry);

      slots.add(slotDef);
    }

    var backSlot = snap.effectiveBackSlot();
    var backTemplate = buildBackTemplate(snap);
    var backDef =
        SlotDefinition.of(backSlot, backTemplate, click -> switchTab(click, InfoTab.CATEGORIES));

    slots.add(backDef);

    return slots;
  }

  private static ItemTemplate buildBackTemplate(@NonNull InfoConfig snap) {
    var material = snap.backMaterial();
    var name = snap.backName();
    var lore = snap.backLore();
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.italic(false);

    return builder.build();
  }

  private SlotDefinition category(
      int slot,
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> lore,
      @NonNull InfoTab target) {
    var loreArray = lore.toArray(String[]::new);

    var templateBuilder = ItemTemplate.builder(icon);
    templateBuilder = templateBuilder.name(name);
    templateBuilder = templateBuilder.lore(loreArray);
    templateBuilder = templateBuilder.italic(false);

    var template = templateBuilder.build();

    return SlotDefinition.of(slot, template, click -> switchTab(click, target));
  }

  private void switchTab(@NonNull ClickContext click, @NonNull InfoTab tab) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();

    this.state.switchTab(viewerId, tab);
    click.refresh();
  }
}
