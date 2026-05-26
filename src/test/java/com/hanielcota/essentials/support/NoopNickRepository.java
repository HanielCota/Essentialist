package com.hanielcota.essentials.support;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public enum NoopNickRepository implements NickRepository {
  INSTANCE;

  @Override
  public List<NickEntry> list() {
    return List.of();
  }

  @Override
  public void save(@NonNull NickEntry entry) {}

  @Override
  public boolean delete(@NonNull UUID id) {
    return false;
  }
}
