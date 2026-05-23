package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Category selector for the material picker. Shows one representative item per category (Combat,
 * Decoration, Minerals, etc). Clicking a category stores the choice in {@link HomesActionTarget}
 * and opens the paginated {@link MaterialPickerMenu}.
 */
@RequiredArgsConstructor
public final class MaterialCategoryMenu implements Menu {

  public static final String ID = "essentials.homes.categories";

  private static final int ROWS = 6;
  private static final List<Integer> CONTENT_SLOTS =
      List.of(
          10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
          38, 39, 40, 41, 42, 43);

  private final MenuService menus;
  private final HomesActionTarget target;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var title = ComponentUtils.mini("<dark_gray>Escolha uma categoria");

    var pagination = PaginationConfig.builder().contentSlots(CONTENT_SLOTS).build();

    MenuFramework.builder(ID, menusRef)
        .rows(ROWS)
        .title(title)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var categories = MaterialCategory.browsable();
    var slots = new ArrayList<SlotDefinition>(categories.size());

    for (var category : categories) {
      if (category == MaterialCategory.MISC) {
        continue;
      }
      var template = representativeItem(category);
      slots.add(
          SlotDefinition.of(
              -1,
              template,
              ctx -> {
                var clicked = ctx.player();
                var clickedUuid = clicked.getUniqueId();
                this.target.setCategory(clickedUuid, category);
                ctx.open(MaterialPickerMenu.ID);
              }));
    }

    return slots;
  }

  private static @NonNull ItemTemplate representativeItem(@NonNull MaterialCategory category) {
    var icon = categoryIcon(category);
    var name = "<gold>" + category.displayName();
    var lore = "<gray>Clique para ver os itens";

    return ItemTemplate.builder(icon).name(name).lore(lore).italic(false).build();
  }

  private static @NonNull Material categoryIcon(@NonNull MaterialCategory category) {
    return switch (category) {
      case CONSTRUCTION -> Material.STONE_BRICKS;
      case WOOD -> Material.OAK_LOG;
      case DECORATION -> Material.WHITE_WOOL;
      case LIGHTING -> Material.LANTERN;
      case COMBAT -> Material.DIAMOND_SWORD;
      case TOOLS -> Material.IRON_PICKAXE;
      case MINERALS -> Material.DIAMOND_BLOCK;
      case REDSTONE -> Material.REDSTONE_BLOCK;
      case FOOD -> Material.GOLDEN_APPLE;
      case TRANSPORT -> Material.MINECART;
      case STORAGE -> Material.ENDER_CHEST;
      case MAGIC -> Material.ENCHANTING_TABLE;
      case NATURE -> Material.GRASS_BLOCK;
      case PLANTS -> Material.OAK_SAPLING;
      case FLOWERS -> Material.POPPY;
      case MISC -> Material.BARRIER;
    };
  }
}
