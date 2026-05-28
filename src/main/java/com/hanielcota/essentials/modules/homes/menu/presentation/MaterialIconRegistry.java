package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.modules.homes.config.menu.HomesPickerMenuConfig;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;

/**
 * One-shot cache of every Minecraft item that can be used as a home icon.
 *
 * <p>Builds {@link ItemTemplate}s once at startup so the paginated menu never touches {@link
 * Material#values()} or string formatting at render time. The cache is category-scoped so players
 * can browse by group (Combat, Decoration, Minerals, etc) instead of scrolling through hundreds of
 * unrelated blocks.
 *
 * <p>Optimisation techniques applied:
 *
 * <ul>
 *   <li>Immutable per-category icon lists — reused on every page turn.
 *   <li>Lazy MISC category — only computed if a material is not in any explicit list.
 *   <li>No runtime string formatting inside the menu render path.
 * </ul>
 */
public final class MaterialIconRegistry {

  private final Map<MaterialCategory, List<MaterialIcon>> iconsByCategory;
  private final List<MaterialIcon> miscIcons;

  public MaterialIconRegistry(
      @NonNull HomesPickerMenuConfig menu, @NonNull MaterialNamesConfig names) {
    this.iconsByCategory = new EnumMap<>(MaterialCategory.class);

    for (var category : MaterialCategory.browsable()) {
      if (category == MaterialCategory.MISC) {
        continue;
      }
      this.iconsByCategory.put(category, buildIcons(category, menu, names));
    }

    this.miscIcons = buildMiscIcons(menu, names);
  }

  private static @NonNull List<MaterialIcon> buildIcons(
      @NonNull MaterialCategory category,
      @NonNull HomesPickerMenuConfig menu,
      @NonNull MaterialNamesConfig names) {

    var icons = new ArrayList<MaterialIcon>(category.materials().size());

    for (var material : category.materials()) {
      if (material.isItem()) {
        var template = renderTemplate(material, menu, names);
        icons.add(new MaterialIcon(material, template));
      }
    }

    return Collections.unmodifiableList(icons);
  }

  private static @NonNull ItemTemplate renderTemplate(
      @NonNull Material material,
      @NonNull HomesPickerMenuConfig menu,
      @NonNull MaterialNamesConfig names) {

    var pretty = names.displayName(material);
    var name = MaterialPickerSection.itemName(menu, pretty);
    var lore = MaterialPickerSection.itemLore(menu, pretty);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore);
    builder.italic(false);

    return builder.build();
  }

  /** All pre-built icons for a category. */
  public @NonNull List<MaterialIcon> iconsFor(@NonNull MaterialCategory category) {
    if (category == MaterialCategory.MISC) {
      return this.miscIcons;
    }
    return this.iconsByCategory.getOrDefault(category, List.of());
  }

  private @NonNull List<MaterialIcon> buildMiscIcons(
      @NonNull HomesPickerMenuConfig menu, @NonNull MaterialNamesConfig names) {
    var known = new ArrayList<Material>();

    for (var category : MaterialCategory.browsable()) {
      if (category != MaterialCategory.MISC) {
        known.addAll(category.materials());
      }
    }

    var icons = new ArrayList<MaterialIcon>();
    for (var material : Material.values()) {
      if (material.isItem() && !known.contains(material)) {
        var template = renderTemplate(material, menu, names);
        icons.add(new MaterialIcon(material, template));
      }
    }

    return Collections.unmodifiableList(icons);
  }

  /** Pair of material + its pre-built template. */
  public record MaterialIcon(@NonNull Material material, @NonNull ItemTemplate template) {}
}
