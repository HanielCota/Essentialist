package com.hanielcota.essentials.modules.msg.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Runtime-only registry of "last conversation partner" per player. Used by {@code /r} to remember
 * who the player last exchanged a private message with. Linking is symmetric — when A messages B,
 * both A's and B's last partner are updated, so either side can /r.
 */
public final class MsgService {

  private final ConcurrentHashMap<UUID, UUID> lastPartner = new ConcurrentHashMap<>();

  public void pair(@NonNull UUID a, @NonNull UUID b) {
    this.lastPartner.put(a, b);
    this.lastPartner.put(b, a);
  }

  public Optional<UUID> lastPartner(@NonNull UUID id) {
    var partner = this.lastPartner.get(id);

    return Optional.ofNullable(partner);
  }

  public void forget(@NonNull UUID id) {
    this.lastPartner.remove(id);
  }
}
