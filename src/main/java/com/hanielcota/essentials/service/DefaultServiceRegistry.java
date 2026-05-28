package com.hanielcota.essentials.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import lombok.NonNull;

public final class DefaultServiceRegistry implements ServiceRegistry {

  private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
  private final List<BiConsumer<Class<?>, Object>> listeners = new CopyOnWriteArrayList<>();

  @Override
  public <T> void register(@NonNull Class<T> type, @NonNull T instance) {
    var previous = this.services.putIfAbsent(type, instance);

    if (previous != null) {
      var typeName = type.getName();
      throw new IllegalStateException("Service already registered: " + typeName);
    }

    notifyListeners(type, instance);
  }

  private void notifyListeners(@NonNull Class<?> type, @NonNull Object instance) {
    for (var listener : this.listeners) {
      listener.accept(type, instance);
    }
  }

  @Override
  public <T> Optional<T> find(@NonNull Class<T> type) {
    var value = this.services.get(type);
    var castedValue = type.cast(value);

    return Optional.ofNullable(castedValue);
  }

  @Override
  public <T> T resolve(@NonNull Class<T> type) {
    var serviceOpt = find(type);
    if (serviceOpt.isPresent()) {
      return serviceOpt.get();
    }

    var typeName = type.getName();
    throw new IllegalStateException("Service not registered: " + typeName);
  }

  @Override
  public <T> boolean unregister(@NonNull Class<T> type) {
    var removedInstance = this.services.remove(type);
    return removedInstance != null;
  }

  @Override
  public Set<Class<?>> registered() {
    var keys = this.services.keySet();
    return Set.copyOf(keys);
  }

  /**
   * Subscribes {@code listener} to future registrations and immediately replays every
   * already-registered service to it, so a late subscriber never misses services registered before
   * it. Replay runs before the listener is wired to avoid double-delivering a concurrent
   * registration.
   */
  @Override
  public void addRegistrationListener(@NonNull BiConsumer<Class<?>, Object> listener) {
    replayExistingTo(listener);

    this.listeners.add(listener);
  }

  private void replayExistingTo(@NonNull BiConsumer<Class<?>, Object> listener) {
    for (var entry : this.services.entrySet()) {
      var type = entry.getKey();
      var instance = entry.getValue();

      listener.accept(type, instance);
    }
  }
}
