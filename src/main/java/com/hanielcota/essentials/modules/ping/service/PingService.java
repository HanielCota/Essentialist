package com.hanielcota.essentials.modules.ping.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ping.config.PingConfig;
import java.util.Objects;

public record PingService(ConfigHandle<PingConfig> config) {

  public PingService {
    Objects.requireNonNull(config, "config");
  }

  public String format(int ping) {
    String color = colorFor(ping);
    return "<" + color + ">" + ping + "ms</" + color + ">";
  }

  private String colorFor(int ping) {
    var snap = config.value();
    if (ping <= snap.goodMaxPing()) {
      return "green";
    }
    if (ping <= snap.mediumMaxPing()) {
      return "yellow";
    }
    return "red";
  }
}
