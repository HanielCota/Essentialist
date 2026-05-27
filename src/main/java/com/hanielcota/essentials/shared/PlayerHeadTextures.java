package com.hanielcota.essentials.shared;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Sidesteps MenuFramework's Steve-head bug.
 *
 * <p>{@code ItemTemplate.Builder#head(UUID)} delegates to {@code
 * Bukkit.getOfflinePlayer(uuid).setOwningPlayer(...)} which doesn't reliably populate the texture
 * under Paper — the {@code OfflinePlayer} profile is lazy, and once the framework caches the
 * resulting {@code ItemStack} by template identity, every subsequent open reuses the same blank
 * profile (Steve). Feeding the base64 texture string instead routes through {@code
 * applyBase64Texture}, which builds a fresh profile with the {@code textures} property set
 * explicitly.
 *
 * <p>Use {@link #applyTo(ItemTemplate.Builder, UUID)} when only a UUID is available; the helper
 * looks up the online player and silently falls back to the UUID path if the player is offline
 * (head will still render as Steve in that case, but that's the best we can do without an external
 * profile fetch).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerHeadTextures {

  private static final String TEXTURES_PROPERTY = "textures";

  public static void applyTo(@NonNull ItemTemplate.Builder builder, @NonNull UUID playerId) {
    var player = Bukkit.getPlayer(playerId);
    if (player != null) {
      applyTo(builder, player);
      return;
    }

    builder.head(playerId);
  }

  public static void applyTo(@NonNull ItemTemplate.Builder builder, @NonNull Player player) {
    var texture = of(player);
    if (texture != null) {
      builder.head(texture);
      return;
    }

    var playerId = player.getUniqueId();
    builder.head(playerId);
  }

  public static @Nullable String of(@NonNull Player player) {
    var profile = player.getPlayerProfile();
    for (var property : profile.getProperties()) {
      if (!TEXTURES_PROPERTY.equals(property.getName())) {
        continue;
      }

      var value = property.getValue();
      if (value == null || value.isEmpty()) {
        return null;
      }

      return value;
    }

    return null;
  }
}
