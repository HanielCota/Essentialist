package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
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

  private static final String LORE_PICK = "<gray>Clique para escolher";

  private final Map<MaterialCategory, List<MaterialIcon>> iconsByCategory;
  private final List<MaterialIcon> miscIcons;

  public MaterialIconRegistry(@NonNull HomesMessages messages) {
    this.iconsByCategory = new EnumMap<>(MaterialCategory.class);

    for (var category : MaterialCategory.browsable()) {
      if (category == MaterialCategory.MISC) {
        continue;
      }
      this.iconsByCategory.put(category, buildIcons(category, messages));
    }

    this.miscIcons = buildMiscIcons(messages);
  }

  /** All pre-built icons for a category. */
  public @NonNull List<MaterialIcon> iconsFor(@NonNull MaterialCategory category) {
    if (category == MaterialCategory.MISC) {
      return this.miscIcons;
    }
    return this.iconsByCategory.getOrDefault(category, List.of());
  }

  /** Total item count inside a category. */
  public int sizeOf(@NonNull MaterialCategory category) {
    return iconsFor(category).size();
  }

  private static @NonNull List<MaterialIcon> buildIcons(
      @NonNull MaterialCategory category, @NonNull HomesMessages messages) {

    var loreTemplate = messages.pickerItemLore();
    var icons = new ArrayList<MaterialIcon>(category.materials().size());

    for (var material : category.materials()) {
      if (material.isItem()) {
        var template = renderTemplate(material, loreTemplate);
        icons.add(new MaterialIcon(material, template));
      }
    }

    return Collections.unmodifiableList(icons);
  }

  private @NonNull List<MaterialIcon> buildMiscIcons(@NonNull HomesMessages messages) {
    var loreTemplate = messages.pickerItemLore();
    var known = new ArrayList<Material>();

    for (var category : MaterialCategory.browsable()) {
      if (category != MaterialCategory.MISC) {
        known.addAll(category.materials());
      }
    }

    var icons = new ArrayList<MaterialIcon>();
    for (var material : Material.values()) {
      if (material.isItem() && !known.contains(material)) {
        var template = renderTemplate(material, loreTemplate);
        icons.add(new MaterialIcon(material, template));
      }
    }

    return Collections.unmodifiableList(icons);
  }

  private static @NonNull ItemTemplate renderTemplate(
      @NonNull Material material, @NonNull String loreTemplate) {

    var pretty = MaterialNames.pretty(material);
    var name = "<gold>" + pretty;
    var lore = loreTemplate.replace("{material}", pretty);

    return ItemTemplate.builder(material).name(name).lore(lore, LORE_PICK).italic(false).build();
  }

  /** Pair of material + its pre-built template. */
  public record MaterialIcon(@NonNull Material material, @NonNull ItemTemplate template) {}
}
