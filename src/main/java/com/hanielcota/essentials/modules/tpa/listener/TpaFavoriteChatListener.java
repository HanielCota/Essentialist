package com.hanielcota.essentials.modules.tpa.listener;

import com.hanielcota.essentials.modules.tpa.command.favorites.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteSessions;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class TpaFavoriteChatListener implements Listener {

  private final TpaFavoritePromptOrchestrator orchestrator;
  private final TpaFavoriteSessions sessions;

  // ignoreCancelled=false so a chat-mute / anti-spam plugin cancelling the message earlier doesn't
  // strand the favorite session waiting for input that will never arrive. The favorite flow has to
  // win the chat regardless of other plugins' decisions; we cancel the event ourselves below.
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

    this.orchestrator.handleInput(player, input);
  }
}
