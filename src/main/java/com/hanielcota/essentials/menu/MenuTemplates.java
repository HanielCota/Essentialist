package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuTemplates {

  /**
   * Conditionally applies a player head to {@code builder}: skipped when {@code icon} is not {@code
   * PLAYER_HEAD}; uses the player's live skin when {@code useSkin} is true; otherwise falls back to
   * {@code headTexture} when non-blank. Centralizes the boilerplate every TPA action-style menu
   * used to copy.
   */
  public static void applyHead(
      @NonNull ItemTemplate.Builder builder,
      @NonNull Material icon,
      boolean useSkin,
      @NonNull String headTexture,
      @NonNull UUID playerId) {
    if (icon != Material.PLAYER_HEAD) {
      return;
    }
    if (useSkin) {
      builder.head(playerId);
      return;
    }
    if (!headTexture.isBlank()) {
      builder.head(headTexture);
    }
  }

  public static @NonNull ItemTemplate simple(@NonNull Material material, @NonNull String name) {
    return simple(material, name, List.of());
  }

  public static @NonNull ItemTemplate simple(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  public static @NonNull ItemTemplate info(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.flags(ItemFlag.HIDE_ATTRIBUTES);
    builder.italic(false);

    return builder.build();
  }
}
