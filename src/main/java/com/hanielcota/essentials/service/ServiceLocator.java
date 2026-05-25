package com.hanielcota.essentials.service;

import java.util.Optional;
import lombok.NonNull;

/**
 * Read-only view of {@link ServiceRegistry}. Consumers that only need to look services up should
 * depend on this narrower interface so they cannot accidentally mutate the registry.
 */
public interface ServiceLocator {

  <T> Optional<T> find(@NonNull Class<T> type);

  <T> T resolve(@NonNull Class<T> type);
}
