package com.hanielcota.essentials.modules.back.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class BackCooldownService {

  private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

  public long remainingSeconds(@NonNull UUID playerId, int cooldownSeconds) {
    if (cooldownSeconds <= 0) {
      return 0;
    }

    var last = this.cooldowns.get(playerId);
    if (last == null) {
      return 0;
    }

    var elapsed = System.currentTimeMillis() - last;
    var remaining = cooldownSeconds - (elapsed / 1000);

    return Math.max(0, remaining);
  }

  public void touch(@NonNull UUID playerId) {
    this.cooldowns.put(playerId, System.currentTimeMillis());
  }

  public void clear(@NonNull UUID playerId) {
    this.cooldowns.remove(playerId);
  }
}
