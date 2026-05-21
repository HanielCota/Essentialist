package com.hanielcota.essentials.user;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UserSessionService {

  private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();

  public Optional<UserSession> sessionOf(UUID id) {
    return Optional.ofNullable(sessions.get(Objects.requireNonNull(id, "id")));
  }

  public UserSession openSession(User user, Locale locale) {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(locale, "locale");
    UserSession session = new UserSession(user, Instant.now(), locale);
    sessions.put(user.id(), session);
    return session;
  }

  public void closeSession(UUID id) {
    sessions.remove(Objects.requireNonNull(id, "id"));
  }
}
