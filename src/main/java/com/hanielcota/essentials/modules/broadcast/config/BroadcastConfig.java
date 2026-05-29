package com.hanielcota.essentials.modules.broadcast.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record BroadcastConfig(
    @Comment("Line sent to every player + console. Placeholder: {message}.") String format,
    @Comment("Shown when no message text was provided.") String usage) {

  public static BroadcastConfig defaults() {
    return new BroadcastConfig(
        "<gray>[<gold>Announcement<gray>] <white>{message}",
        "<red>Provide the announcement message.");
  }

  public String formatLine(@NonNull String message) {
    return format.replace("{message}", message);
  }
}
