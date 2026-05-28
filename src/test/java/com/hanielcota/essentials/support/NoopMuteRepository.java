package com.hanielcota.essentials.support;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public enum NoopMuteRepository implements MuteRepository {
  INSTANCE;

  @Override
  public List<Map.Entry<UUID, Mute>> listActive(@NonNull Instant now) {
    return List.of();
  }

  @Override
  public Optional<Mute> findActive(@NonNull UUID id, @NonNull Instant now) {
    return Optional.empty();
  }

  @Override
  public void save(@NonNull UUID id, @NonNull Mute mute) {}

  @Override
  public boolean delete(@NonNull UUID id) {
    return false;
  }

  @Override
  public int deleteExpired(@NonNull Instant now) {
    return 0;
  }
}
