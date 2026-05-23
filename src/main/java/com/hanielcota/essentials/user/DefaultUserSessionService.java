package com.hanielcota.essentials.user;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultUserSessionService implements UserSessionService {

  private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();

  @Override
  public Optional<UserSession> sessionOf(UUID id) {
    return Optional.ofNullable(sessions.get(id));
  }

  @Override
  public UserSession openSession(UUID playerId) {
    var session = new UserSession(playerId, Instant.now());
    sessions.put(playerId, session);
    return session;
  }

  @Override
  public void closeSession(UUID id) {
    sessions.remove(id);
  }
}
