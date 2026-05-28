package com.hanielcota.essentials.support;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public enum NoopNickRepository implements NickRepository {
  INSTANCE;

  @Override
  public List<NickEntry> list() {
    return List.of();
  }

  @Override
  public Optional<NickEntry> findById(@NonNull UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<UUID> idByNickname(@NonNull String nickname) {
    return Optional.empty();
  }

  @Override
  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    return false;
  }

  @Override
  public void save(@NonNull NickEntry entry) {}

  @Override
  public boolean delete(@NonNull UUID id) {
    return false;
  }
}
