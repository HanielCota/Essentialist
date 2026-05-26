package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Global broadcast — every player on the server (and the console) sees the message. No viewer
 * filtering required since the default {@link AsyncChatEvent#viewers()} already contains the full
 * audience.
 */
public final class GlobalChannel implements ChatChannel {

  public static final String ID = "global";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public String template(@NonNull ChatConfig config) {
    return config.global().format();
  }

  @Override
  public void filterViewers(@NonNull AsyncChatEvent event, @NonNull Player sender) {
    // Intentionally empty — global keeps the default audience.
  }
}
