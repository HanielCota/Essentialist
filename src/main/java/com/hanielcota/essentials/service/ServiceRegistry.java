package com.hanielcota.essentials.service;

import java.util.Set;
import lombok.NonNull;

/**
 * Write-side of the service registry. Extends {@link ServiceLocator} so a single registry instance
 * satisfies both reads and writes. Bootstrap and modules depend on this interface; runtime
 * consumers should depend on {@link ServiceLocator}.
 */
public interface ServiceRegistry extends ServiceLocator {

  <T> void register(@NonNull Class<T> type, @NonNull T instance);

  <T> boolean unregister(@NonNull Class<T> type);

  Set<Class<?>> registered();
}
