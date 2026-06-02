package com.hanielcota.essentials.modules.ban.listener;

import com.hanielcota.essentials.modules.ban.command.BanNotifier;
import com.hanielcota.essentials.modules.ban.service.BanService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/** Denies login to banned accounts with the ban screen. Runs before whitelist/other gates. */
@RequiredArgsConstructor
public final class BanLoginListener implements Listener {

  private final BanService service;
  private final BanNotifier notifier;

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLogin(@NonNull AsyncPlayerPreLoginEvent event) {
    var id = event.getUniqueId();
    var ban = this.service.activeBan(id).orElse(null);

    if (ban == null) {
      return;
    }

    var screen = this.notifier.kickComponent(ban);

    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, screen);
  }
}
