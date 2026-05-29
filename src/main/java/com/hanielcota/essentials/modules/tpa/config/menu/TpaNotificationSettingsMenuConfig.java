package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Notification settings sub-menu — controls sound and chat-alert toggles. Opened from {@code
 * TpaSettingsMenu}.
 */
@ConfigSerializable
public record TpaNotificationSettingsMenuConfig(
    @Comment("Notification settings menu title.") String title,
    @Comment("Notification settings menu rows (1-6).") int rows,
    @Comment("Slot of the notification sound toggle.") int soundsSlot,
    @Comment("Slot of the notify-when-favorited toggle.") int notifyWhenFavoritedSlot,
    @Comment("Material used when a toggle is enabled.") Material enabledIcon,
    @Comment("Material used when a toggle is disabled.") Material disabledIcon,
    @Comment("Name of the sound toggle. Placeholder: {state}.") String soundsName,
    @Comment("Lore of the sound toggle. Placeholder: {state}.") List<String> soundsLore,
    @Comment("Name of the notify-when-favorited toggle. Placeholder: {state}.")
        String notifyWhenFavoritedName,
    @Comment("Lore of the notify-when-favorited toggle. Placeholder: {state}.")
        List<String> notifyWhenFavoritedLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label used when a toggle is enabled.") String enabledLabel,
    @Comment("Label used when a toggle is disabled.") String disabledLabel) {

  public static TpaNotificationSettingsMenuConfig defaults() {
    return new TpaNotificationSettingsMenuConfig(
        "Notifications",
        3,
        10,
        12,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "Notification sounds: {state}",
        List.of("Plays a sound when you receive a TPA request.", "", "Click to turn on or off."),
        "Notify when favorited: {state}",
        List.of("Get a chat message when someone favorites you.", "", "Click to turn on or off."),
        22,
        Material.ARROW,
        "Back",
        List.of("Back to the settings."),
        "on",
        "off");
  }
}
