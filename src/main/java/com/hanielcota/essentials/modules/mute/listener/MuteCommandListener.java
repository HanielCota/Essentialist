package com.hanielcota.essentials.modules.mute.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.command.MuteBlockMessageRenderer;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.service.MuteCommandLineParser;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Blocks chat-equivalent commands ({@code /me}, {@code /tell}, {@code /say}, ...) for muted
 * players. Without this, {@code MuteChatListener} alone leaves an obvious bypass: a muted player
 * can still broadcast via {@code /me <text>}.
 *
 * <p>Runs at {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} for symmetry with
 * {@code MuteChatListener} and so it does not fight earlier listeners that consume the command line
 * as input (e.g. a chat-prompt listener registered at LOWEST).
 */
@RequiredArgsConstructor
public final class MuteCommandListener implements Listener {

  private final ConfigHandle<MuteConfig> config;
  private final MuteService service;
  private final MuteBlockMessageRenderer renderer;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onCommand(@NonNull PlayerCommandPreprocessEvent event) {
    var commandName = MuteCommandLineParser.canonicalName(event.getMessage());
    if (commandName.isEmpty()) {
      return;
    }

    var snap = this.config.value();
    if (!snap.blockedCommands().isBlocked(commandName)) {
      return;
    }

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
