package com.hanielcota.essentials.modules.broadcast.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.broadcast.config.BroadcastConfig;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Renders a broadcast template and sends the line to the server-wide audience (every online player
 * plus console). MiniMessage tags in the user-provided message are honored, matching the existing
 * convention of {@code /title} and {@code /actionbar broadcast}.
 */
@RequiredArgsConstructor
public final class BroadcastService {

  private final ConfigHandle<BroadcastConfig> config;
  private final AudienceProvider audiences;

  public void broadcast(@NonNull String message) {
    var snap = this.config.value();
    var line = snap.formatLine(message);
    var component = ComponentUtils.mini(line);
    var audience = this.audiences.broadcast();

    audience.sendMessage(component);
  }
}
