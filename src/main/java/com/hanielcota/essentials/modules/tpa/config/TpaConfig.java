package com.hanielcota.essentials.modules.tpa.config;

import com.hanielcota.essentials.modules.tpa.config.menu.TpaBehaviorSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaBlockedMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaFavoriteActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaFavoritesMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaHelpInfoMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaNotificationSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingActionMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPendingMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPickPlayerMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPrivacySettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaProfileMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaTargetActionMenuConfig;
import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the {@code /tpa} module: the request lifetime plus three sections — {@link
 * TpaMessages} (every chat line), {@link TpaMenuConfig} (the {@code /tpahistory} inventory), {@link
 * TpaHelpMenuConfig} (the {@code /tpa} hub), {@link TpaPendingMenuConfig} (incoming requests),
 * {@link TpaBlockedMenuConfig} (specific blocked players) and {@link TpaSettingsMenuConfig}
 * (per-player receive toggles).
 */
@ConfigSerializable
public record TpaConfig(
    @Comment("How long a pending request lives before expiring, in seconds (minimum 5).")
        int requestExpirySeconds,
    @Comment("How long the favorite-add prompt waits for chat input, in seconds (minimum 5).")
        int favoritePromptSeconds,
    @Comment(
            "Minimum seconds between two /tpa or /tpahere sends by the same player. Applies to"
                + " every entry point (command, target action menu, hub last-contacted, favorite"
                + " action menu) so menus cannot bypass the command's cooldown. Set to 0 to"
                + " disable.")
        int sendCooldownSeconds,
    TpaMessages messages,
    TpaMenuConfig menu,
    TpaHelpMenuConfig helpMenu,
    TpaPendingMenuConfig pendingMenu,
    TpaBlockedMenuConfig blockedMenu,
    TpaSettingsMenuConfig settingsMenu,
    TpaFavoritesMenuConfig favoritesMenu,
    TpaFavoriteActionMenuConfig favoriteActionMenu,
    TpaTargetActionMenuConfig targetActionMenu,
    TpaPickPlayerMenuConfig pickPlayerMenu,
    TpaPendingActionMenuConfig pendingActionMenu,
    TpaPrivacySettingsMenuConfig privacySettingsMenu,
    TpaNotificationSettingsMenuConfig notificationSettingsMenu,
    TpaBehaviorSettingsMenuConfig behaviorSettingsMenu,
    TpaProfileMenuConfig profileMenu,
    TpaHelpInfoMenuConfig helpInfoMenu) {

  public static TpaConfig defaults() {
    return new TpaConfig(
        60,
        30,
        5,
        TpaMessages.defaults(),
        TpaMenuConfig.defaults(),
        TpaHelpMenuConfig.defaults(),
        TpaPendingMenuConfig.defaults(),
        TpaBlockedMenuConfig.defaults(),
        TpaSettingsMenuConfig.defaults(),
        TpaFavoritesMenuConfig.defaults(),
        TpaFavoriteActionMenuConfig.defaults(),
        TpaTargetActionMenuConfig.defaults(),
        TpaPickPlayerMenuConfig.defaults(),
        TpaPendingActionMenuConfig.defaults(),
        TpaPrivacySettingsMenuConfig.defaults(),
        TpaNotificationSettingsMenuConfig.defaults(),
        TpaBehaviorSettingsMenuConfig.defaults(),
        TpaProfileMenuConfig.defaults(),
        TpaHelpInfoMenuConfig.defaults());
  }

  /** Configured request lifetime, clamped to a sane minimum. */
  public Duration requestExpiry() {
    return Duration.ofSeconds(Math.max(5, requestExpirySeconds));
  }

  /** Configured timeout for the favorite-add chat prompt, clamped to a sane minimum. */
  public Duration favoritePromptTimeout() {
    return Duration.ofSeconds(Math.max(5, favoritePromptSeconds));
  }

  /** Configured per-sender send cooldown. {@link Duration#ZERO} disables the rate limit. */
  public Duration sendCooldown() {
    return Duration.ofSeconds(Math.max(0, sendCooldownSeconds));
  }
}
