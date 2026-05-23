package com.hanielcota.essentials.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class DefaultServiceRegistry implements ServiceRegistry {

  private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

  @Override
  public <T> void register(@NonNull Class<T> type, @NonNull T instance) {
    var previous = services.putIfAbsent(type, instance);

    if (previous != null) {
      var typeName = type.getName();
      throw new IllegalStateException("Service already registered: " + typeName);
    }
  }

  @Override
  public <T> Optional<T> find(@NonNull Class<T> type) {
    var value = services.get(type);
    var castedValue = type.cast(value);

    return Optional.ofNullable(castedValue);
  }

  @Override
  public <T> T resolve(@NonNull Class<T> type) {
    var serviceOpt = find(type);

    return serviceOpt.orElseThrow(
        () -> {
          var typeName = type.getName();
          return new IllegalStateException("Service not registered: " + typeName);
        });
  }

  @Override
  public <T> boolean unregister(@NonNull Class<T> type) {
    var removedInstance = services.remove(type);
    return removedInstance != null;
  }

  @Override
  public Set<Class<?>> registered() {
    var keys = services.keySet();
    return Set.copyOf(keys);
  }
}
