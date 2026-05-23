package com.hanielcota.essentials.modules.homes.config;

import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
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
    @Comment("Home name used when /sethome and /home are run with no argument.")
        String defaultHomeName,
    @Comment(
            "Default home limit per player when they have no essentials.home.limit.N permission. "
                + "Set to 0 to require permissions for every home.")
        int defaultLimit,
    @Comment("Icon for new homes when /sethome is called without a material.")
        Material defaultMaterial,
    @Comment("Seconds the rename prompt waits for the player's next chat message. 0 disables.")
        int renameTimeoutSeconds,
    HomesMenuConfig menu,
    HomesMessages messages) {

  public static HomesConfig defaults() {
    return new HomesConfig(
        3, "home", 1, Material.RED_BED, 30, HomesMenuConfig.defaults(), HomesMessages.defaults());
  }

  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }

  public Duration renameTimeout() {
    return Duration.ofSeconds(Math.max(0, renameTimeoutSeconds));
  }
}
