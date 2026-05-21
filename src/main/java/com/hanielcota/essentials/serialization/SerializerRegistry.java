package com.hanielcota.essentials.serialization;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SerializerRegistry {

  private final Map<Class<?>, Serializer<?>> serializers = new ConcurrentHashMap<>();

  public <T> void register(Serializer<T> serializer) {
    Objects.requireNonNull(serializer, "serializer");
    serializers.put(serializer.type(), serializer);
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<Serializer<T>> find(Class<T> type) {
    Objects.requireNonNull(type, "type");
    return Optional.ofNullable((Serializer<T>) serializers.get(type));
  }

  public <T> Serializer<T> resolve(Class<T> type) {
    return this.find(type)
        .orElseThrow(
            () -> new IllegalStateException("No serializer registered for " + type.getName()));
  }
}
