package com.hanielcota.essentials.modules.ban.listener;

import com.hanielcota.essentials.modules.ban.menu.BanNickOrchestrator;
import com.hanielcota.essentials.modules.ban.service.BanNickSessions;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Captures the next chat line from a staff member who armed the nick search and routes it to the
 * orchestrator instead of broadcasting it. {@code ignoreCancelled = false} so a chat-mute plugin
 * doesn't strand a session waiting on input that never arrives.
 */
@RequiredArgsConstructor
public final class BanNickChatListener implements Listener {

  private final BanNickSessions sessions;
  private final BanNickOrchestrator orchestrator;

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
  public void onChat(@NonNull AsyncChatEvent event) {
    var player = event.getPlayer();
    var consumed = this.sessions.consume(player.getUniqueId());

    if (!consumed) {
      return;
    }

    event.setCancelled(true);

    var serializer = PlainTextComponentSerializer.plainText();
    var input = serializer.serialize(event.message()).strip();

    this.orchestrator.handleInput(player, input);
  }
}
