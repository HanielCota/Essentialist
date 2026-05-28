package com.hanielcota.essentials.modules.homes.menu.options;

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
import com.hanielcota.essentials.modules.homes.config.menu.HomesOptionsMenuConfig;
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
    var options = snap.menu().options();
    var rows = MenuLayouts.clampRows(options.rows());

    var titleTemplate = options.title();
    var titleRaw = stripNamePlaceholder(titleTemplate);
    var title = ComponentUtils.mini(titleRaw);

    var contentSlots =
        List.of(
            MenuLayouts.sanitizeSlot(options.homeSlot(), rows, 4),
            MenuLayouts.sanitizeSlot(options.teleportSlot(), rows, 11),
            MenuLayouts.sanitizeSlot(options.renameSlot(), rows, 12),
            MenuLayouts.sanitizeSlot(options.pinSlot(), rows, 13),
            MenuLayouts.sanitizeSlot(options.iconSlot(), rows, 14),
            MenuLayouts.sanitizeSlot(options.deleteSlot(), rows, 15),
            MenuLayouts.sanitizeSlot(options.backSlot(), rows, 22));
    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var options = this.config.value().menu().options();
    var rows = MenuLayouts.clampRows(options.rows());

    var uuid = player.getUniqueId();
    var homeName = this.target.peek(uuid);

    var pinned = isPinned(uuid, homeName);

    var slots = new ArrayList<SlotDefinition>();
    slots.add(homeSlot(options, rows, uuid, homeName));
    slots.add(teleportSlot(options, rows, homeName));
    slots.add(renameSlot(options, rows, homeName));
    slots.add(pinSlot(options, rows, homeName, pinned));
    slots.add(iconSlot(options, rows, homeName));
    slots.add(deleteSlot(options, rows, homeName));
    slots.add(backSlot(options, rows));
    return slots;
  }

  private boolean isPinned(@NonNull java.util.UUID uuid, String homeName) {
    if (homeName == null) {
      return false;
    }
    return this.service.findHome(uuid, homeName).map(home -> home.pinned()).orElse(false);
  }

  private SlotDefinition homeSlot(
      @NonNull HomesOptionsMenuConfig options,
      int rows,
      @NonNull java.util.UUID uuid,
      String homeName) {
    var safeSlot = MenuLayouts.sanitizeSlot(options.homeSlot(), rows, 4);
    if (homeName == null) {
      return SlotDefinition.of(safeSlot, missingTemplate(options), click -> {});
    }

    var home = this.service.findHome(uuid, homeName);
    if (home.isEmpty()) {
      return SlotDefinition.of(safeSlot, missingTemplate(options), click -> {});
    }

    var template = this.renderer.render(home.get());
    return SlotDefinition.of(safeSlot, template, click -> {});
  }

  private SlotDefinition teleportSlot(
      @NonNull HomesOptionsMenuConfig options, int rows, String homeName) {
    var template =
        buttonTemplate(
            options.teleportMaterial(), options.teleportName(), options.teleportLore(), homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(options.teleportSlot(), rows, 11);

    return SlotDefinition.of(safeSlot, template, this.clicks::teleport);
  }

  private SlotDefinition renameSlot(
      @NonNull HomesOptionsMenuConfig options, int rows, String homeName) {
    var template =
        buttonTemplate(
            options.renameMaterial(), options.renameName(), options.renameLore(), homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(options.renameSlot(), rows, 12);

    return SlotDefinition.of(safeSlot, template, this.clicks::rename);
  }

  private SlotDefinition pinSlot(
      @NonNull HomesOptionsMenuConfig options, int rows, String homeName, boolean pinned) {
    var material = pinned ? options.unpinMaterial() : options.pinMaterial();
    var nameTemplate = pinned ? options.unpinName() : options.pinName();
    var loreTemplate = pinned ? options.unpinLore() : options.pinLore();

    var template = buttonTemplate(material, nameTemplate, loreTemplate, homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(options.pinSlot(), rows, 13);

    return SlotDefinition.of(safeSlot, template, this.clicks::togglePin);
  }

  private SlotDefinition iconSlot(
      @NonNull HomesOptionsMenuConfig options, int rows, String homeName) {
    var template =
        buttonTemplate(options.iconMaterial(), options.iconName(), options.iconLore(), homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(options.iconSlot(), rows, 14);

    return SlotDefinition.of(safeSlot, template, this.clicks::changeIcon);
  }

  private SlotDefinition deleteSlot(
      @NonNull HomesOptionsMenuConfig options, int rows, String homeName) {
    var template =
        buttonTemplate(
            options.deleteMaterial(), options.deleteName(), options.deleteLore(), homeName);
    var safeSlot = MenuLayouts.sanitizeSlot(options.deleteSlot(), rows, 15);

    return SlotDefinition.of(safeSlot, template, this.clicks::delete);
  }

  private SlotDefinition backSlot(@NonNull HomesOptionsMenuConfig options, int rows) {
    var template =
        MenuTemplates.simple(options.backMaterial(), options.backName(), options.backLore());
    var safeSlot = MenuLayouts.sanitizeSlot(options.backSlot(), rows, 22);

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

  private static ItemTemplate missingTemplate(@NonNull HomesOptionsMenuConfig options) {
    return MenuTemplates.simple(options.backMaterial(), "<red>Home indisponível", List.of());
  }

  // The inventory title is fixed at menu registration, so the {name} placeholder cannot be
  // resolved per-viewer. The home name is shown in the info slot instead — strip the unresolved
  // token so existing user configs don't render it literally.
  private static String stripNamePlaceholder(@NonNull String raw) {
    return raw.replace("{name}", "").strip();
  }
}
