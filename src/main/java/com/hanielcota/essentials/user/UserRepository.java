package com.hanielcota.essentials.user;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  Optional<User> findById(UUID id);

  Optional<User> findByName(String name);

  Collection<User> all();

  User save(User user);
}
