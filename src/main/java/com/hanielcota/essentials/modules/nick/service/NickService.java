package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.api.NicksApi;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickCache;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Thin facade for the nickname system. In-memory caching and persistence coordination are delegated
 * to {@link NickCache}.
 */
@RequiredArgsConstructor
public final class NickService implements NicksApi {

  private final @NonNull NickCache cache;

  public Optional<NickEntry> nickOf(@NonNull UUID id) {
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
