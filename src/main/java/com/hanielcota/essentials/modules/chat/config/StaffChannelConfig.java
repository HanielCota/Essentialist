package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record StaffChannelConfig(
    @Comment(
            "MiniMessage format template for the staff channel. Only players with"
                + " chat.staff.receive see messages on this channel.")
        String format,
    @Comment(
            "Cooldown in seconds between messages on this channel. 0 disables. Players with"
                + " chat.staff.bypasscooldown ignore the cooldown.")
        int cooldownSeconds,
    @Comment("Shown to a staff member when toggling persistent staff chat ON.") String toggleOn,
    @Comment("Shown to a staff member when toggling persistent staff chat OFF.") String toggleOff,
    @Comment("Shown when /staffchat is used without a message.") String usage) {

  public static StaffChannelConfig defaults() {
    return new StaffChannelConfig(
        "<dark_red>[S]</dark_red> <gold><player></gold> <dark_gray>»</dark_gray>"
            + " <yellow><message></yellow>",
        0,
        "<green>Persistent staff chat ENABLED.",
        "<green>Persistent staff chat DISABLED.",
        "<yellow>Use <gray>/staffchat <message></gray> or <gray>/staffchat toggle</gray>.");
  }
}
