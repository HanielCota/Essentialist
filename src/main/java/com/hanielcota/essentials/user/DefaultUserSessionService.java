package com.hanielcota.essentials.user;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class DefaultUserSessionService implements UserSessionService {

  private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();

  @Override
  public Optional<UserSession> sessionOf(@NonNull UUID id) {
    var session = sessions.get(id);
    return Optional.ofNullable(session);
  }

  @Override
  public UserSession openSession(@NonNull UUID playerId) {
    var currentTime = Instant.now();
    var session = new UserSession(playerId, currentTime);

    sessions.put(playerId, session);
    return session;
  }

  @Override
  public void closeSession(@NonNull UUID id) {
    sessions.remove(id);
  }
}
