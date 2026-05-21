package com.hanielcota.essentials.user;

import java.util.Objects;
import java.util.Optional;

public record UserContext(User actor, Optional<UserSession> session) {

  public UserContext {
    Objects.requireNonNull(actor, "actor");
    Objects.requireNonNull(session, "session");
  }

  public static UserContext of(User actor) {
    return new UserContext(actor, Optional.empty());
  }

  public static UserContext of(User actor, UserSession session) {
    return new UserContext(actor, Optional.of(session));
  }
}
