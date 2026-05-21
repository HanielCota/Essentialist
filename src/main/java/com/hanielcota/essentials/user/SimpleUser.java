package com.hanielcota.essentials.user;

import java.util.Objects;
import java.util.UUID;

public record SimpleUser(UUID id, String name) implements User {

  public SimpleUser {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");
  }
}
