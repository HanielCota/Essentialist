package com.hanielcota.essentials.service;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.NonNull;

public interface ServiceRegistry {

  <T> void register(@NonNull Class<T> type, @NonNull T instance);

  <T> Optional<T> find(@NonNull Class<T> type);

  <T> T resolve(@NonNull Class<T> type);

  <T> boolean unregister(@NonNull Class<T> type);

  Set<Class<?>> registered();

  /**
   * Subscribes to future {@link #register(Class, Object)} calls and immediately replays every
   * already-registered service to the listener, so a late subscriber sees the full set regardless
   * of registration order. The listener is invoked synchronously with the declared type and
   * instance. Useful for mirroring services into adjacent containers (e.g. the command framework's
   * DI table) without coupling those callbacks to the registration call sites.
   */
  void addRegistrationListener(@NonNull BiConsumer<Class<?>, Object> listener);
}
