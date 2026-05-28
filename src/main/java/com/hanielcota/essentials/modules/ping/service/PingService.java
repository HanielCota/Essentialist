package com.hanielcota.essentials.modules.ping.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ping.config.PingConfig;

public record PingService(ConfigHandle<PingConfig> config) {

  public String format(int ping) {
    // Player#getPing() can transiently report -1 before the first keep-alive sample; clamp so it
    // never renders as a green "-1ms".
    var safePing = Math.max(0, ping);

    var color = colorFor(safePing);
    var openTag = "<" + color + ">";
    var closeTag = "</" + color + ">";
    return openTag + safePing + "ms" + closeTag;
  }

  private String colorFor(int ping) {
    var snap = this.config.value();
    if (ping <= snap.goodMaxPing()) {
      return "green";
    }
    if (ping <= snap.mediumMaxPing()) {
      return "yellow";
    }
    return "red";
  }
}
