package com.hanielcota.essentials.modules.info.menu.presentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Pulls the base64 {@code textures} property off an online {@link Player}'s live profile so we can
 * feed it directly to {@code ItemTemplate.Builder#head(String)} — that path runs through
 * MenuFramework's {@code applyBase64Texture} which builds a fresh profile with the texture set,
 * unlike the UUID path which delegates to a lazy {@code OfflinePlayer} and often renders Steve.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PlayerHeadTextures {

  static @Nullable String of(@NonNull Player player) {
    var profile = player.getPlayerProfile();
    for (var property : profile.getProperties()) {
      if (!"textures".equals(property.getName())) {
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
