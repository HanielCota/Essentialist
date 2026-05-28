package com.hanielcota.essentials.modules.homes.config;

import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import java.time.Duration;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the homes module: the warm-up delay, the default name used by {@code /sethome} and
 * {@code /home} with no argument, the fallback per-player limit, the menu settings and every chat
 * line ({@link HomesMessages}).
 */
@ConfigSerializable
public record HomesConfig(
    @Comment("Warm-up delay in seconds before /home teleports. 0 disables the delay.")
        int teleportDelaySeconds,
    @Comment(
            "Default home limit per player when they have no essentials.home.limit.N permission. "
                + "Set to 0 to require permissions for every home.")
        int defaultLimit,
    @Comment("Icon used for new homes created from the /homes + button.") Material defaultMaterial,
    @Comment(
            "Seconds the rename and create prompts wait for the player's next chat message. "
                + "0 disables.")
        int renameTimeoutSeconds,
    @Comment("Minimum length of a home name.") int homeNameMinLength,
    @Comment("Maximum length of a home name.") int homeNameMaxLength,
    @Comment("Regex a home name must fully match. Default allows letters, digits, '_' and '-'.")
        String allowedNamePattern,
    HomesMenuConfig menu,
    HomesMessages messages) {

  public static HomesConfig defaults() {
    var menu = HomesMenuConfig.defaults();
    var messages = HomesMessages.defaults();

    return new HomesConfig(3, 1, Material.RED_BED, 30, 1, 32, "[A-Za-z0-9_-]+", menu, messages);
  }

  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }

  public Duration renameTimeout() {
    return Duration.ofSeconds(Math.max(0, renameTimeoutSeconds));
  }
}
