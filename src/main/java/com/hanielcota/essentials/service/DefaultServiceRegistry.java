package com.hanielcota.essentials.service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultServiceRegistry implements ServiceRegistry {

  private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

  @Override
  public <T> void register(Class<T> type, T instance) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(instance, "instance");
    var previous = services.putIfAbsent(type, instance);
    if (previous != null) {
      throw new IllegalStateException("Service already registered: " + type.getName());
    }
  }

  @Override
  public <T> Optional<T> find(Class<T> type) {
    var value = services.get(Objects.requireNonNull(type, "type"));
    return Optional.ofNullable(type.cast(value));
  }

  @Override
  public <T> T resolve(Class<T> type) {
    return find(type)
        .orElseThrow(() -> new IllegalStateException("Service not registered: " + type.getName()));
  }

  @Override
  public <T> boolean unregister(Class<T> type) {
    return services.remove(Objects.requireNonNull(type, "type")) != null;
  }

  @Override
  public Set<Class<?>> registered() {
    return Set.copyOf(services.keySet());
  }
}
