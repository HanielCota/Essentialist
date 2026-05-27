package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.create.HomeCreateOrchestrator;
import com.hanielcota.essentials.modules.homes.create.HomeCreateSessions;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class HomeCreateChatListener implements Listener {

  private final HomeCreateOrchestrator create;
  private final HomeCreateSessions sessions;

  // Same reasoning as HomeRenameChatListener: ignoreCancelled=false so chat-mute plugins don't
  // strand the create session waiting on input that never arrives.
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
  public void onChat(@NonNull AsyncChatEvent event) {
    var player = event.getPlayer();
    var session = this.sessions.consume(player.getUniqueId());

    if (session == null) {
      return;
    }

    session.timeoutTask().cancel();
    event.setCancelled(true);

    var serializer = PlainTextComponentSerializer.plainText();
    var input = serializer.serialize(event.message()).strip();

    this.create.handleInput(player, session.location(), input);
  }
}
