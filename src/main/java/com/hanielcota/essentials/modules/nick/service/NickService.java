package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/**
 * Thin facade for the nickname system. In-memory caching and persistence coordination are delegated
 * to {@link NickCacheStore}.
 */
public final class NickService {

  private final NickCacheStore cache;

  public NickService(@NonNull NickCacheStore cache) {
    this.cache = cache;
  }

  public Optional<NickEntry> nickFor(@NonNull UUID id) {
    return this.cache.nickFor(id);
  }

  public Optional<UUID> idByNick(@NonNull String nickname) {
    return this.cache.idByNick(nickname);
  }

  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    return this.cache.isTakenByOther(nickname, self);
  }

  public void set(@NonNull UUID id, @NonNull String nickname, @NonNull String realName) {
    this.cache.set(id, nickname, realName);
  }

  public boolean reset(@NonNull UUID id) {
    return this.cache.reset(id);
  }
}
