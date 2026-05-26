package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Releases the in-memory chat-state entries a player accumulates during a session:
 *
 * <ul>
 *   <li>{@link StaffChatToggleService} — drops the persistent staff toggle so it does not carry
 *       across reconnects (spec: "persistente por sessão").
 *   <li>{@link CooldownService} — frees the per-player {@code long[]} of last-send timestamps.
 *   <li>{@link AntiSpamService} — frees the per-player last-message reference.
 * </ul>
 *
 * <p>Three services + one listener instead of three listeners: the cleanup contract is a single
 * concern ("forget the disconnected player"), so keeping the side-effects together makes the
 * lifecycle obvious and avoids three near-identical files.
 */
@RequiredArgsConstructor
public final class ChatPlayerCleanupListener implements Listener {

  private final StaffChatToggleService staffToggle;
  private final CooldownService cooldowns;
  private final AntiSpamService antiSpam;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.staffToggle.clear(playerId);
    this.cooldowns.clear(playerId);
    this.antiSpam.clear(playerId);
  }
}
