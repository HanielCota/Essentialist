package com.hanielcota.essentials.service;

import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

public interface ServiceRegistry {

  <T> void register(@NonNull Class<T> type, @NonNull T instance);

  <T> Optional<T> find(@NonNull Class<T> type);

  <T> T resolve(@NonNull Class<T> type);

  <T> boolean unregister(@NonNull Class<T> type);

  Set<Class<?>> registered();
}
