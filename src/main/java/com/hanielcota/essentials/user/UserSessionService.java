package com.hanielcota.essentials.user;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

public interface UserSessionService {

  Optional<UserSession> sessionOf(@NonNull UUID id);

  UserSession openSession(@NonNull UUID playerId);

  void closeSession(@NonNull UUID id);
}
