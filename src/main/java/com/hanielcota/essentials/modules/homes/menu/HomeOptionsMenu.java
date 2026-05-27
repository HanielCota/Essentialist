package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Right-click sub-menu of /homes. Shows the home info plus four action buttons (Teleport, Rename,
 * Change icon, Delete) and a Back button. The active home name is read from {@link
 * HomesActionTarget} on each render so every page refresh picks up the latest target.
 */
@RequiredArgsConstructor
public final class HomeOptionsMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes.options";

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeEntryRenderer renderer;
  private final HomesActionTarget target;
  private final HomeOptionsClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menuSpec = snap.menu();
    var rows = MenuLayouts.clampRows(menuSpec.optionsRows());

    var titleTemplate = menuSpec.optionsTitle();
    var title = ComponentUtils.mini(titleTemplate);

    var contentSlots =
        List.of(
            MenuLayouts.sanitizeSlot(menuSpec.optionsHomeSlot(), rows, 4),
            MenuLayouts.sanitizeSlot(menuSpec.optionsTeleportSlot(), rows, 11),
            MenuLayouts.sanitizeSlot(menuSpec.optionsRenameSlot(), rows, 12),
            MenuLayouts.sanitizeSlot(menuSpec.optionsIconSlot(), rows, 14),
            MenuLayouts.sanitizeSlot(menuSpec.optionsDeleteSlot(), rows, 15),
            MenuLayouts.sanitizeSlot(menuSpec.optionsBackSlot(), rows, 22));
    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var menuSpec = snap.menu();
    var rows = MenuLayouts.clampRows(menuSpec.optionsRows());

    var uuid = player.getUniqueId();
    var homeName = this.target.peek(uuid);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(homeSlot(menuSpec, rows, uuid, homeName));
    slots.add(teleportSlot(menuSpec, rows, homeName));
    slots.add(renameSlot(menuSpec, rows, homeName));
    slots.add(iconSlot(menuSpec, rows, homeName));
    slots.add(deleteSlot(menuSpec, rows, homeName));
    slots.add(backSlot(menuSpec, rows));
    return slots;
  }

  private SlotDefinition homeSlot(
      @NonNull HomesMenuConfig menuSpec, int rows, @NonNull java.util.UUID uuid, String homeName) {
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsHomeSlot(), rows, 4);
    if (homeName == null) {
      return SlotDefinition.of(safeSlot, missingTemplate(menuSpec), click -> {});
    }

    var home = this.service.findHome(uuid, homeName);
    if (home.isEmpty()) {
      return SlotDefinition.of(safeSlot, missingTemplate(menuSpec), click -> {});
    }

    var template = this.renderer.render(home.get());
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition teleportSlot(
      @NonNull HomesMenuConfig menuSpec, int rows, String homeName) {
    var template =
        buttonTemplate(
            menuSpec.optionsTeleportMaterial(),
            menuSpec.optionsTeleportName(),
            menuSpec.optionsTeleportLore(),
            homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsTeleportSlot(), rows, 11);

    return SlotDefinition.of(safeSlot, template, this.clicks::teleport);
  }

  private SlotDefinition renameSlot(@NonNull HomesMenuConfig menuSpec, int rows, String homeName) {
    var template =
        buttonTemplate(
            menuSpec.optionsRenameMaterial(),
            menuSpec.optionsRenameName(),
            menuSpec.optionsRenameLore(),
            homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsRenameSlot(), rows, 12);

    return SlotDefinition.of(safeSlot, template, this.clicks::rename);
  }

  private SlotDefinition iconSlot(@NonNull HomesMenuConfig menuSpec, int rows, String homeName) {
    var template =
        buttonTemplate(
            menuSpec.optionsIconMaterial(),
            menuSpec.optionsIconName(),
            menuSpec.optionsIconLore(),
            homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsIconSlot(), rows, 14);

    return SlotDefinition.of(safeSlot, template, this.clicks::changeIcon);
  }

  private SlotDefinition deleteSlot(@NonNull HomesMenuConfig menuSpec, int rows, String homeName) {
    var template =
        buttonTemplate(
            menuSpec.optionsDeleteMaterial(),
            menuSpec.optionsDeleteName(),
            menuSpec.optionsDeleteLore(),
            homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsDeleteSlot(), rows, 15);

    return SlotDefinition.of(safeSlot, template, this.clicks::delete);
  }

  private SlotDefinition backSlot(@NonNull HomesMenuConfig menuSpec, int rows) {
    var template =
        MenuTemplates.simple(
            menuSpec.optionsBackMaterial(), menuSpec.optionsBackName(), menuSpec.optionsBackLore());
    var safeSlot = MenuLayouts.sanitizeSlot(menuSpec.optionsBackSlot(), rows, 22);

    return SlotDefinition.of(safeSlot, template, this.clicks::back);
  }

  private static ItemTemplate buttonTemplate(
      @NonNull org.bukkit.Material material,
      @NonNull String nameTemplate,
      @NonNull List<String> loreTemplate,
      String homeName) {
    var safeName = homeName != null ? homeName : "?";
    var name = nameTemplate.replace("{name}", safeName);
    var lore = Placeholders.replaceInAll(loreTemplate, "{name}", safeName);

    return MenuTemplates.simple(material, name, lore);
  }

  private static ItemTemplate missingTemplate(@NonNull HomesMenuConfig menuSpec) {
    return MenuTemplates.simple(
        menuSpec.optionsBackMaterial(), "<red>Home indisponível", List.of());
  }
}
