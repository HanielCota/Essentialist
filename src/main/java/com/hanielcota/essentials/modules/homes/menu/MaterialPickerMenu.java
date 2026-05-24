package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialIconRegistry;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Paginated material picker. Shows every Minecraft item from the category the player chose in
 * {@link MaterialCategoryMenu}.
 *
 * <p>Performance optimisations from MenuFramework:
 *
 * <ul>
 *   <li>{@link PaginationConfig} with a dense content-slot grid — maximises items per page.
 *   <li>{@link MaterialIconRegistry} pre-builds every {@link SlotDefinition} at plugin startup; the
 *       dynamic-content provider only copies the cached list, never touches {@link
 *       Material#values()} or string formatting.
 *   <li>The pagination engine handles page splitting and nav buttons natively — no manual page-math
 *       in application code.
 * </ul>
 */
@RequiredArgsConstructor
public final class MaterialPickerMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes.picker";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<HomesConfig> config;
  private final HomesActionTarget target;
  private final MaterialIconRegistry registry;
  private final MaterialPickerClickHandler clickHandler;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var menuSpec = this.config.value().menu();
    var menuTitle = ComponentUtils.mini(menuSpec.staticPickerTitle());
    var rows = menuSpec.effectivePickerRows();

    var paginationBuilder =
        PaginationConfig.builder().contentSlots(menuSpec.effectivePickerContentSlots());
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menusRef, paginationBuilder, ID, rows, menuSpec.pickerNavigation());
    }
    var pagination = paginationBuilder.build();

    MenuFramework.builder(ID, menusRef)
        .rows(rows)
        .title(menuTitle)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var category = this.target.peekCategory(uuid);

    if (category == null) {
      return List.of();
    }

    var icons = this.registry.iconsFor(category);
    var slots = new java.util.ArrayList<SlotDefinition>(icons.size() + 1);

    for (var icon : icons) {
      var pickedMaterial = icon.material();
      slots.add(
          SlotDefinition.of(
              -1, icon.template(), ctx -> this.clickHandler.pick(ctx, pickedMaterial)));
    }

    slots.add(backButtonSlot());

    return slots;
  }

  private @NonNull SlotDefinition backButtonSlot() {
    var menuSpec = this.config.value().menu();
    var back =
        ItemTemplate.builder(menuSpec.pickerBackMaterial())
            .name(menuSpec.pickerBackName())
            .lore(menuSpec.pickerBackLore().toArray(String[]::new))
            .italic(false)
            .build();

    return SlotDefinition.of(menuSpec.effectivePickerBackSlot(), back, this.clickHandler::back);
  }
}
