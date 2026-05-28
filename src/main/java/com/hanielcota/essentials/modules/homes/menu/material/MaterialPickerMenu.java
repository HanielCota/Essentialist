package com.hanielcota.essentials.modules.homes.menu.material;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
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
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerSection;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
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
    var snap = this.config.value();
    var pickerMenu = snap.menu().picker();

    var titleText = MaterialPickerSection.staticTitle(pickerMenu);
    var menuTitle = ComponentUtils.mini(titleText);
    var rows = MaterialPickerSection.rows(pickerMenu);
    var contentSlots = MaterialPickerSection.contentSlots(pickerMenu);

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      var navigation = pickerMenu.navigation();
      PageNavigation.apply(menusRef, paginationBuilder, ID, rows, navigation);
    }
    var pagination = paginationBuilder.build();

    var builder = MenuFramework.builder(ID, menusRef);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var backBtn = backButtonSlot();
    builder.slot(backBtn.slot(), backBtn.template(), backBtn.handler());

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var category = this.target.peekCategory(uuid);

    if (category == null) {
      return List.of();
    }

    var icons = this.registry.iconsFor(category);
    var slots = new ArrayList<SlotDefinition>(icons.size());

    for (var icon : icons) {
      var pickedMaterial = icon.material();
      var template = icon.template();
      ClickHandler onPick = ctx -> this.clickHandler.pick(ctx, pickedMaterial);
      var slot = SlotDefinition.of(-1, template, onPick);

      slots.add(slot);
    }

    return slots;
  }

  private @NonNull SlotDefinition backButtonSlot() {
    var pickerMenu = this.config.value().menu().picker();

    var material = pickerMenu.backMaterial();
    var name = pickerMenu.backName();
    var lore = pickerMenu.backLore();
    var loreArray = lore.toArray(String[]::new);

    var templateBuilder = ItemTemplate.builder(material);
    templateBuilder.name(name);
    templateBuilder.lore(loreArray);
    templateBuilder.italic(false);
    var template = templateBuilder.build();

    var slot = MaterialPickerSection.backSlot(pickerMenu);

    return SlotDefinition.of(slot, template, this.clickHandler::back);
  }
}
