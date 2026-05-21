package com.hanielcota.essentials.user;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

public record UserSession(User user, Instant connectedAt, Locale locale) {

  public UserSession {
    Objects.requireNonNull(user, "user");
    Objects.requireNonNull(connectedAt, "connectedAt");
    Objects.requireNonNull(locale, "locale");
  }
}
