package com.hanielcota.essentials.service;

import java.util.Optional;
import java.util.Set;

public interface ServiceRegistry {

  <T> void register(Class<T> type, T instance);

  <T> Optional<T> find(Class<T> type);

  <T> T resolve(Class<T> type);

  <T> boolean unregister(Class<T> type);

  Set<Class<?>> registered();
}
