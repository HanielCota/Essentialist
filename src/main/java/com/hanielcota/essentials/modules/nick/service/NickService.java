package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.api.NicksApi;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Thin facade for the nickname system. Persistence and caching live behind the {@link
 * NickRepository} abstraction; this service exposes the domain-shaped operations.
 */
@RequiredArgsConstructor
public final class NickService implements NicksApi {

  private final @NonNull NickRepository repository;

  public Optional<NickEntry> nickOf(@NonNull UUID id) {
    return this.repository.findById(id);
  }

  public Optional<UUID> idByNick(@NonNull String nickname) {
    return this.repository.idByNickname(nickname);
  }

  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    return this.repository.isTakenByOther(nickname, self);
  }

  public void set(@NonNull UUID id, @NonNull String nickname, @NonNull String realName) {
    var entry = new NickEntry(id, nickname, realName);
    this.repository.save(entry);
  }

  public boolean reset(@NonNull UUID id) {
    return this.repository.delete(id);
  }
}
