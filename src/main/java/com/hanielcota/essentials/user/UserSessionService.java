package com.hanielcota.essentials.user;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionService {

  Optional<UserSession> sessionOf(UUID id);

  UserSession openSession(UUID playerId);

  void closeSession(UUID id);
}
