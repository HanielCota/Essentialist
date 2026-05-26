package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public interface RequestStore {

  void add(@NonNull TeleportRequest request);

  boolean delete(@NonNull TeleportRequest request);

  List<TeleportRequest> incomingFor(@NonNull UUID target);

  Optional<TeleportRequest> incomingFrom(@NonNull UUID target, @NonNull String requesterName);

  Optional<TeleportRequest> outgoingOf(@NonNull UUID requester);

  List<TeleportRequest> expiredAt(@NonNull Instant now);

  List<TeleportRequest> involving(@NonNull UUID player);
}
