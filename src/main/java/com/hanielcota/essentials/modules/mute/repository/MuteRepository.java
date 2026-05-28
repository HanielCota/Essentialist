package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public interface MuteRepository {

  List<Map.Entry<UUID, Mute>> listActive(@NonNull Instant now);

  Optional<Mute> findActive(@NonNull UUID id, @NonNull Instant now);

  void save(@NonNull UUID id, @NonNull Mute mute);

  boolean delete(@NonNull UUID id);

  int deleteExpired(@NonNull Instant now);
}
