package com.hanielcota.essentials.modules.ban.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** A clickable ban reason in the options menu. The {@code reason} is what gets stored and shown. */
@ConfigSerializable
public record BanReasonOption(
    @Comment("Icon shown in the reason grid.") Material icon,
    @Comment("Display name of the button (MiniMessage).") String name,
    @Comment("The reason text stored on the ban and shown to the target.") String reason,
    @Comment("Extra lore lines (MiniMessage).") List<String> lore) {

  public static List<BanReasonOption> defaults() {
    return List.of(
        new BanReasonOption(
            Material.IRON_SWORD, "<red>Cheating", "Cheating / unfair advantage", List.of()),
        new BanReasonOption(Material.PAPER, "<gold>Spam", "Spamming", List.of()),
        new BanReasonOption(
            Material.BOOK, "<yellow>Offensive language", "Offensive language", List.of()),
        new BanReasonOption(Material.TNT, "<dark_red>Griefing", "Griefing", List.of()),
        new BanReasonOption(Material.OAK_SIGN, "<aqua>Advertising", "Advertising", List.of()),
        new BanReasonOption(
            Material.BARRIER,
            "<light_purple>Inappropriate behaviour",
            "Inappropriate behaviour",
            List.of()));
  }
}
