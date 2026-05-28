package com.hanielcota.essentials.modules.mute.config;

import java.util.List;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record MuteBlockedCommands(
    @Comment(
            "Commands a muted player cannot run (names without the leading slash). Matched case-"
                + "insensitively against the first token, with any namespace prefix stripped (so "
                + "\"me\" also catches \"minecraft:me\").")
        List<String> commands) {

  public static MuteBlockedCommands defaults() {
    return new MuteBlockedCommands(
        List.of("me", "tell", "w", "whisper", "msg", "say", "r", "reply"));
  }

  public boolean isBlocked(@NonNull String commandName) {
    for (var blocked : this.commands) {
      if (blocked.equalsIgnoreCase(commandName)) {
        return true;
      }
    }
    return false;
  }
}
