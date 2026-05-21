package com.hanielcota.essentials.serialization;

public interface Serializer<T> {

  Class<T> type();

  String serialize(T value);

  T deserialize(String raw);
}
