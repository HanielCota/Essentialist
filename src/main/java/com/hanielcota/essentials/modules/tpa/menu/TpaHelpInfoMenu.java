package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaHelpInfoMenuConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Help/tutorial screen opened from the "Como funciona" slot in {@code TpaHelpMenu}. Shows three
 * static cards (commands, examples, FAQ) plus a back button.
 */
@RequiredArgsConstructor
public final class TpaHelpInfoMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpa.help.info";

  private final ConfigHandle<TpaConfig> config;

  static List<Integer> contentSlots(@NonNull TpaHelpInfoMenuConfig settings, int rows) {
    return List.of(
        MenuLayouts.sanitizeSlot(settings.commandsSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.examplesSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.faqSlot(), rows, 0),
        MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().helpInfoMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(ComponentUtils.mini(settings.title()));
    builder.pagination(
        PaginationConfig.builder().contentSlots(contentSlots(settings, rows)).build());
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var settings = this.config.value().helpInfoMenu();
    var rows = MenuLayouts.clampRows(settings.rows());

    return List.of(
        cardSlot(
            settings.commandsSlot(),
            rows,
            settings.commandsIcon(),
            settings.commandsName(),
            settings.commandsLore()),
        cardSlot(
            settings.examplesSlot(),
            rows,
            settings.examplesIcon(),
            settings.examplesName(),
            settings.examplesLore()),
        cardSlot(
            settings.faqSlot(), rows, settings.faqIcon(), settings.faqName(), settings.faqLore()),
        backSlot(settings, rows));
  }

  private SlotDefinition cardSlot(
      int configuredSlot,
      int rows,
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> lore) {
    var template = MenuTemplates.simple(icon, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition backSlot(@NonNull TpaHelpInfoMenuConfig settings, int rows) {
    var template =
        MenuTemplates.simple(settings.backIcon(), settings.backName(), settings.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, click -> click.switchTo(TpaHelpMenu.ID));
  }
}
