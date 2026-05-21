package com.hanielcota.essentials.user;

import java.util.Objects;
import java.util.UUID;

public record UserService(UserRepository repository) {

  public UserService {
    Objects.requireNonNull(repository, "repository");
  }

  public User getOrCreate(UUID id, String name) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");
    return repository.findById(id).orElseGet(() -> repository.save(new SimpleUser(id, name)));
  }
}
