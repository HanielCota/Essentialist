package com.hanielcota.essentials.service;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * Type-keyed registry of shared services. A given type may be registered at most once; modules
 * publish their services here on enable and remove them on disable.
 */
public interface ServiceRegistry {

  /**
   * Publishes {@code instance} under {@code type}.
   *
   * @throws IllegalStateException if a service is already registered for {@code type}
   */
  <T> void register(@NonNull Class<T> type, @NonNull T instance);

  /** Looks up the service for {@code type}, or empty when none is registered. */
  <T> Optional<T> find(@NonNull Class<T> type);

  /**
   * Looks up a required service.
   *
   * @return the registered instance, never {@code null}
   * @throws IllegalStateException if no service is registered for {@code type}
   */
  <T> T resolve(@NonNull Class<T> type);

  /**
   * Removes the service registered for {@code type}.
   *
   * @return {@code true} if a service was present and removed, {@code false} otherwise
   */
  <T> boolean unregister(@NonNull Class<T> type);

  /** Snapshot of the currently registered service types. */
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
