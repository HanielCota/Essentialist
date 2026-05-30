package com.hanielcota.essentials.modules.ban.repository;

import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public interface BanRepository {

  List<ActiveBan> listActive(@NonNull Instant now);

  Optional<Ban> findActive(@NonNull UUID id, @NonNull Instant now);

  void save(@NonNull ActiveBan ban);

  boolean delete(@NonNull UUID id);

  int deleteExpired(@NonNull Instant now);
}
