package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Settings hub — navigation only. Opens one of three category sub-menus: privacy, notifications or
 * behavior. Cooldown info is shown as a read-only item; the back button returns to {@code
 * TpaHelpMenu}.
 */
@ConfigSerializable
public record TpaSettingsMenuConfig(
    @Comment("TPA settings hub title.") String title,
    @Comment("TPA settings hub rows (1-6).") int rows,
    @Comment("Slot of the privacy category button.") int privacySlot,
    @Comment("Material of the privacy category button.") Material privacyIcon,
    @Comment("Name of the privacy category button.") String privacyName,
    @Comment("Lore of the privacy category button.") List<String> privacyLore,
    @Comment("Slot of the notifications category button.") int notificationsSlot,
    @Comment("Material of the notifications category button.") Material notificationsIcon,
    @Comment("Name of the notifications category button.") String notificationsName,
    @Comment("Lore of the notifications category button.") List<String> notificationsLore,
    @Comment("Slot of the behavior category button.") int behaviorSlot,
    @Comment("Material of the behavior category button.") Material behaviorIcon,
    @Comment("Name of the behavior category button.") String behaviorName,
    @Comment("Lore of the behavior category button.") List<String> behaviorLore,
    @Comment("Slot of the read-only cooldown info item.") int cooldownSlot,
    @Comment("Material of the cooldown info item.") Material cooldownIcon,
    @Comment("Name of the cooldown info item.") String cooldownName,
    @Comment("Lore of the cooldown info item.") List<String> cooldownLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaSettingsMenuConfig defaults() {
    return new TpaSettingsMenuConfig(
        "TPA Settings",
        3,
        10,
        Material.IRON_DOOR,
        "Privacy",
        List.of("Controls who can send you TPA requests.", "", "Click to open."),
        12,
        Material.BELL,
        "Notifications",
        List.of("Sounds and chat alerts.", "", "Click to open."),
        14,
        Material.REDSTONE_TORCH,
        "Behavior",
        List.of("Auto-accept favorites and Do Not Disturb mode.", "", "Click to open."),
        20,
        Material.CLOCK,
        "Cooldown",
        List.of("Wait time between commands: 5 seconds.", "", "(configured by the server)"),
        22,
        Material.ARROW,
        "Back",
        List.of("Back to the TPA menu."));
  }
}
