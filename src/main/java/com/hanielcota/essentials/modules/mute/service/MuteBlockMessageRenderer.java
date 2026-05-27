package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.DurationFormatter;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

@RequiredArgsConstructor
public final class MuteBlockMessageRenderer {

  private final ConfigHandle<MuteConfig> config;

  public Component render(@NonNull Mute mute) {
    var snap = this.config.value();
    var expiresAt = mute.expiresAt();

    if (expiresAt == null) {
      var permanentMsg = snap.chatBlocked();
      var permanentComponent = ComponentUtils.mini(permanentMsg);

      return permanentComponent;
    }

    var now = Instant.now();
    var remaining = Duration.between(now, expiresAt);
    var timeStr = DurationFormatter.format(remaining);
    var timedMsg = snap.formatChatBlockedTimed(timeStr);
    var timedComponent = ComponentUtils.mini(timedMsg);

    return timedComponent;
  }
}
