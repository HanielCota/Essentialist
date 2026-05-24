package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Server-controlled display names for every material the homes picker can show. Lives in its own
 * YAML ({@code homes/material-names.yml}) so admins can relocalize the icon picker without touching
 * the rest of {@code homes.yml}. Materials missing from the map fall back to a title-cased version
 * of their enum name (e.g. {@code DARK_OAK_LOG} → "Dark Oak Log").
 */
@ConfigSerializable
public record MaterialNamesConfig(
    @Comment("Display name shown for each material in the home icon picker.")
        Map<Material, String> names) {

  public static MaterialNamesConfig defaults() {
    var map = new EnumMap<Material, String>(Material.class);
    for (var category : MaterialCategory.values()) {
      for (var material : category.materials()) {
        map.put(material, toDisplayName(material));
      }
    }
    return new MaterialNamesConfig(map);
  }

  public String displayName(@NonNull Material material) {
    var configured = names.get(material);
    return configured != null ? configured : toDisplayName(material);
  }

  private static String toDisplayName(@NonNull Material material) {
    var raw = material.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    var words = raw.split(" ");
    var sb = new StringBuilder(raw.length());

    for (var i = 0; i < words.length; i++) {
      var word = words[i];
      if (word.isEmpty()) {
        continue;
      }

      if (i > 0) {
        sb.append(' ');
      }
      sb.append(Character.toUpperCase(word.charAt(0)));
      if (word.length() > 1) {
        sb.append(word, 1, word.length());
      }
    }
    return sb.toString();
  }
}
