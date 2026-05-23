package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class HomeRenameChatListener implements Listener {

  private final HomeRenameOrchestrator rename;
  private final HomeRenameSessions sessions;

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    var player = event.getPlayer();
    var session = sessions.consume(player.getUniqueId());

    if (session == null) {
      return;
    }

    session.timeoutTask().cancel();
    event.setCancelled(true);

    var serializer = PlainTextComponentSerializer.plainText();
    var input = serializer.serialize(event.message()).strip();

    rename.handleInput(player, session.homeName(), input);
  }
}
