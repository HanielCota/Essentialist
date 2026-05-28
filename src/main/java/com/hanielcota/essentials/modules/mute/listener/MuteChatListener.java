package com.hanielcota.essentials.modules.mute.listener;

import com.hanielcota.essentials.modules.mute.command.MuteBlockMessageRenderer;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Cancels chat from muted players and notifies them inline.
 *
 * <p>Runs at {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} so it doesn't fight
 * earlier listeners that may consume the chat as input (e.g. {@code HomeRenameChatListener} at
 * {@link EventPriority#LOWEST}). {@code Player#sendMessage(Component)} is safe to call from the
 * async chat thread under Paper.
 */
@RequiredArgsConstructor
public final class MuteChatListener implements Listener {

  private final MuteService service;
  private final MuteBlockMessageRenderer renderer;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    var mute = this.service.activeMute(id).orElse(null);
    if (mute == null) {
      return;
    }

    event.setCancelled(true);

    var message = this.renderer.render(mute);

    player.sendMessage(message);
  }
}
