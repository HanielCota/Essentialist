package com.hanielcota.essentials.message;

import java.util.Objects;

public record MessageKey(String namespace, String path) {

  public MessageKey {
    Objects.requireNonNull(namespace, "namespace");
    Objects.requireNonNull(path, "path");
    if (namespace.isBlank() || path.isBlank()) {
      throw new IllegalArgumentException("namespace/path must not be blank");
    }
  }

  public static MessageKey of(String namespace, String path) {
    return new MessageKey(namespace, path);
  }

  public String full() {
    return namespace + "." + path;
  }
}
