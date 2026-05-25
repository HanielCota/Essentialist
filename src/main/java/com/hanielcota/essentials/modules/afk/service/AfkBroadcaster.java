package com.hanielcota.essentials.modules.afk.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.afk.config.AfkConfig;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Renders the AFK enter/exit lines and sends them server-wide via {@link AudienceProvider}. Kept
 * separate from {@link AfkService} so the service stays pure state + the broadcaster owns
 * formatting + dispatch.
 */
@RequiredArgsConstructor
public final class AfkBroadcaster {

  private final ConfigHandle<AfkConfig> config;
  private final AudienceProvider audiences;

  public void broadcastEnter(@NonNull String playerName, @Nullable String reason) {
    var snap = this.config.value();
    var line =
        reason == null
            ? snap.formatEnter(playerName)
            : snap.formatEnterWithReason(playerName, reason);
    var component = ComponentUtils.mini(line);
    var audience = this.audiences.broadcast();

    audience.sendMessage(component);
  }

  public void broadcastExit(@NonNull String playerName) {
    var snap = this.config.value();
    var line = snap.formatExit(playerName);
    var component = ComponentUtils.mini(line);
    var audience = this.audiences.broadcast();

    audience.sendMessage(component);
  }
}
