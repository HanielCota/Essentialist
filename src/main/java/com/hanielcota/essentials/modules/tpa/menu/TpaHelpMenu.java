package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.builder.MenuBuilder;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaHelpMenuConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * Help / how-to menu shown when {@code /tpa} is invoked without a target. Three slots are static
 * explainers (no-op click) and the history slot opens the {@link TpaHistoryMenu}.
 */
@RequiredArgsConstructor
public final class TpaHelpMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.help";

  private final ConfigHandle<TpaConfig> config;

  private static ItemTemplate template(
      @NonNull Material icon, @NonNull String name, @NonNull List<String> lore) {
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(icon);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);

    return builder.build();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var helpMenu = snap.helpMenu();

    var rows = MenuLayouts.clampRows(helpMenu.rows());

    var rawTitle = helpMenu.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);

    placeStatic(
        builder,
        rows,
        helpMenu.tpaSlot(),
        template(helpMenu.tpaIcon(), helpMenu.tpaName(), helpMenu.tpaLore()));
    placeStatic(
        builder,
        rows,
        helpMenu.tpaHereSlot(),
        template(helpMenu.tpaHereIcon(), helpMenu.tpaHereName(), helpMenu.tpaHereLore()));
    placeStatic(
        builder,
        rows,
        helpMenu.acceptSlot(),
        template(helpMenu.acceptIcon(), helpMenu.acceptName(), helpMenu.acceptLore()));
    placeHistory(builder, rows, helpMenu);

    var menu = builder.build();
    menu.register();
  }

  private void placeStatic(
      @NonNull MenuBuilder builder, int rows, int slot, @NonNull ItemTemplate template) {
    var safeSlot = MenuLayouts.sanitizeSlot(slot, rows, 0);
    builder.slot(safeSlot, template, click -> {});
  }

  private void placeHistory(
      @NonNull MenuBuilder builder, int rows, @NonNull TpaHelpMenuConfig helpMenu) {
    var historyTemplate =
        template(helpMenu.historyIcon(), helpMenu.historyName(), helpMenu.historyLore());
    var safeSlot = MenuLayouts.sanitizeSlot(helpMenu.historySlot(), rows, 0);

    builder.slot(safeSlot, historyTemplate, this::openHistory);
  }

  private void openHistory(@NonNull ClickContext click) {
    click.switchTo(TpaHistoryMenu.ID);
  }
}
