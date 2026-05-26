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
import com.hanielcota.essentials.modules.tpa.config.TpaBlockedMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TpaBlockedMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.blocked";

  private final ConfigHandle<TpaConfig> config;
  private final TpaBlockService blocks;

  static List<Integer> contentSlots(@NonNull TpaBlockedMenuConfig settings, int rows) {
    var fallback = MenuLayouts.fallbackContentSlots(rows, Math.min(7, MenuLayouts.slotCount(rows)));

    return MenuLayouts.sanitizeSlots(settings.contentSlots(), rows, fallback);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().blockedMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(
        backSlot(settings, rows),
        backTemplate(settings),
        click -> click.switchTo(TpaSettingsMenu.ID));

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var entries = this.blocks.blockedBy(player.getUniqueId());
    if (entries.isEmpty()) {
      return emptySlot();
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var entry : entries) {
      slots.add(blockedSlot(entry));
    }
    return slots;
  }

  private SlotDefinition blockedSlot(@NonNull TpaBlockService.Entry entry) {
    var template = blockedTemplate(entry);

    return SlotDefinition.of(-1, template, click -> unblock(click, entry));
  }

  private void unblock(@NonNull ClickContext click, @NonNull TpaBlockService.Entry entry) {
    var playerId = click.player().getUniqueId();
    this.blocks.unblock(playerId, entry.blockedId());
    click.reply(
        this.config.value().messages().unblockedPlayer().replace("{player}", entry.blockedName()));
    click.refresh();
  }

  private List<SlotDefinition> emptySlot() {
    var settings = this.config.value().blockedMenu();
    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = contentSlots(settings, rows);
    var center = slots.get(slots.size() / 2);

    return List.of(SlotDefinition.of(center, emptyTemplate(settings), click -> {}));
  }

  private ItemTemplate blockedTemplate(@NonNull TpaBlockService.Entry entry) {
    var settings = this.config.value().blockedMenu();
    var name = settings.blockedName().replace("{player}", entry.blockedName());
    var lore = replacePlayer(settings.blockedLore(), entry.blockedName());

    var builder = ItemTemplate.builder(settings.blockedIcon());
    applyHead(builder, settings, entry);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static void applyHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull TpaBlockedMenuConfig settings,
      @NonNull TpaBlockService.Entry entry) {
    if (settings.blockedIcon() != Material.PLAYER_HEAD) {
      return;
    }
    if (settings.blockedUsePlayerHead()) {
      builder.head(entry.blockedId());
      return;
    }
    if (!settings.blockedHeadTexture().isBlank()) {
      builder.head(settings.blockedHeadTexture());
    }
  }

  private static List<String> replacePlayer(@NonNull List<String> lines, @NonNull String player) {
    var replaced = new ArrayList<String>(lines.size());
    for (var line : lines) {
      replaced.add(line.replace("{player}", player));
    }
    return replaced;
  }

  private static ItemTemplate emptyTemplate(@NonNull TpaBlockedMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.emptyIcon());
    builder.name(settings.emptyName());
    builder.lore(settings.emptyLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static int backSlot(@NonNull TpaBlockedMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
  }

  private static ItemTemplate backTemplate(@NonNull TpaBlockedMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.backIcon());
    builder.name(settings.backName());
    builder.lore(settings.backLore().toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }
}
