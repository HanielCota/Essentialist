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
 * via {@link ClickContext#refresh()} â€” it never opens a separate menu, which keeps the
 * framework's navigation history and session intact.
 */
@RequiredArgsConstructor
public final class InfoMenu implements EssentialsMenu {

  public static final String ID = "essentials.info";

  private final ConfigHandle<InfoConfig> config;
  private final InfoService service;
  private final InfoMenuState state;

  private static SlotDefinition entryItem(int slot, @NonNull InfoEntry entry) {
    var builder = ItemTemplate.builder(entry.icon());
    builder = builder.name(entry.name());
    builder = builder.lore(entry.lore().toArray(String[]::new));
    builder = builder.italic(false);

    if (entry.headOwner() != null) {
      builder.head(entry.headOwner());
    }
    return SlotDefinition.of(slot, builder.build(), click -> {});
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var pagBuilder = PaginationConfig.builder();
    pagBuilder = pagBuilder.contentSlots(snap.effectiveContentSlots());
    var pagination = pagBuilder.build();

    var menuTitle = ComponentUtils.mini(snap.menuTitle());
    var menuBuilder = MenuFramework.builder(ID, menus);
    menuBuilder = menuBuilder.rows(snap.effectiveRows());
    menuBuilder = menuBuilder.title(menuTitle);
    menuBuilder = menuBuilder.pagination(pagination);
    menuBuilder = menuBuilder.dynamicContent(this::buildSlots);
    var menu = menuBuilder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    InfoTab tab = this.state.tab(player.getUniqueId());
    return switch (tab) {
      case CATEGORIES -> categorySlots();
      case SERVER -> detailSlots(this.service.serverEntries());
      case PLAYER -> detailSlots(this.service.playerEntries(this.state.resolveTarget(player)));
      case ABOUT -> detailSlots(this.service.aboutEntries());
    };
  }

  private List<SlotDefinition> categorySlots() {
    var snap = this.config.value();
    return List.of(
        category(
            snap.effectiveServerSlot(),
            snap.serverMaterial(),
            snap.serverName(),
            snap.serverLore(),
            InfoTab.SERVER),
        category(
            snap.effectivePlayerSlot(),
            snap.playerMaterial(),
            snap.playerName(),
            snap.playerLore(),
            InfoTab.PLAYER),
        category(
            snap.effectiveAboutSlot(),
            snap.aboutMaterial(),
            snap.aboutName(),
            snap.aboutLore(),
            InfoTab.ABOUT));
  }

  private List<SlotDefinition> detailSlots(@NonNull List<InfoEntry> entries) {
    var snap = this.config.value();
    var detailSlots = snap.effectiveDetailSlots();
    var visibleEntries = Math.min(entries.size(), detailSlots.size());
    var startIdx = (detailSlots.size() - visibleEntries) / 2;
    var slots = new ArrayList<SlotDefinition>(visibleEntries + 1);
    for (var i = 0; i < visibleEntries; i++) {
      slots.add(entryItem(detailSlots.get(startIdx + i), entries.get(i)));
    }
    var backBuilder = ItemTemplate.builder(snap.backMaterial());
    backBuilder = backBuilder.name(snap.backName());
    var backLoreArray = snap.backLore().toArray(String[]::new);
    backBuilder = backBuilder.lore(backLoreArray);
    backBuilder = backBuilder.italic(false);
    var back = backBuilder.build();
    slots.add(
        SlotDefinition.of(
            snap.effectiveBackSlot(), back, click -> switchTab(click, InfoTab.CATEGORIES)));
    return slots;
  }

  private SlotDefinition category(
      int slot,
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> lore,
      @NonNull InfoTab target) {
    var templateBuilder = ItemTemplate.builder(icon);
    templateBuilder = templateBuilder.name(name);
    templateBuilder = templateBuilder.lore(lore.toArray(String[]::new));
    templateBuilder = templateBuilder.italic(false);
    var template = templateBuilder.build();
    return SlotDefinition.of(slot, template, click -> switchTab(click, target));
  }

  private void switchTab(@NonNull ClickContext click, @NonNull InfoTab tab) {
    this.state.switchTab(click.player().getUniqueId(), tab);
    click.refresh();
  }
}
