package com.hanielcota.essentials.user;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUserRepository implements UserRepository {

  private final Map<UUID, User> byId = new ConcurrentHashMap<>();
  private final Map<String, UUID> byName = new ConcurrentHashMap<>();

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(byId.get(Objects.requireNonNull(id, "id")));
  }

  @Override
  public Optional<User> findByName(String name) {
    Objects.requireNonNull(name, "name");
    UUID id = byName.get(name.toLowerCase(Locale.ROOT));
    return id == null ? Optional.empty() : findById(id);
  }

  @Override
  public Collection<User> all() {
    return List.copyOf(byId.values());
  }

  @Override
  public User save(User user) {
    Objects.requireNonNull(user, "user");
    User previous = byId.put(user.id(), user);
    if (previous != null && !previous.name().equalsIgnoreCase(user.name())) {
      byName.remove(previous.name().toLowerCase(Locale.ROOT));
    }
    byName.put(user.name().toLowerCase(Locale.ROOT), user.id());
    return user;
  }
}
