package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TpaBlockedMenuConfig(
    @Comment("Blocked players menu title.") String title,
    @Comment("Blocked players menu rows (1-6).") int rows,
    @Comment("Slots used by blocked player items.") List<Integer> contentSlots,
    @Comment("Material of each blocked player item.") Material blockedIcon,
    @Comment("Use the blocked player's skin on blocked player items.") boolean blockedUsePlayerHead,
    @Comment("Custom head texture when blockedIcon is PLAYER_HEAD and player skin is disabled.")
        String blockedHeadTexture,
    @Comment("Blocked player item name. Placeholder: {player}.") String blockedName,
    @Comment("Blocked player item lore. Placeholder: {player}.") List<String> blockedLore,
    @Comment("Material of the placeholder shown when no players are blocked.") Material emptyIcon,
    @Comment("Name of the placeholder shown when no players are blocked.") String emptyName,
    @Comment("Lore of the placeholder shown when no players are blocked.") List<String> emptyLore,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore) {

  public static TpaBlockedMenuConfig defaults() {
    return new TpaBlockedMenuConfig(
        "Blocked players",
        5,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25),
        Material.PLAYER_HEAD,
        true,
        "",
        "{player}",
        List.of("This player cannot send you TPA requests.", "", "Click to unblock."),
        Material.BARRIER,
        "Nobody blocked",
        List.of("Your block list is empty.", "", "Use /tpablock <player> to block someone."),
        40,
        Material.ARROW,
        "Back",
        List.of("Back to the settings."));
  }
}
