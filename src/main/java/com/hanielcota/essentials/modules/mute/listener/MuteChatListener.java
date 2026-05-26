package com.hanielcota.essentials.modules.mute.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.DurationFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.Nullable;

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

  private final ConfigHandle<MuteConfig> config;
  private final MuteService service;

  private static String renderBlocked(@NonNull MuteConfig snap, @Nullable Instant expiresAt) {
    if (expiresAt == null) {
      return snap.chatBlocked();
    }

    var now = Instant.now();
    var remaining = Duration.between(now, expiresAt);
    var timeStr = DurationFormatter.format(remaining);

    return snap.formatChatBlockedTimed(timeStr);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    var mute = this.service.activeMute(id).orElse(null);
    if (mute == null) {
      return;
    }

    event.setCancelled(true);

    var snap = this.config.value();
    var line = renderBlocked(snap, mute.expiresAt());
    var component = ComponentUtils.mini(line);

    player.sendMessage(component);
  }
}
