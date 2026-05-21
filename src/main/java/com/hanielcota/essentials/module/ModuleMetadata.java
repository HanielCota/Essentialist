package com.hanielcota.essentials.module;

import java.util.Objects;
import java.util.Set;

public record ModuleMetadata(
    String id, Set<String> dependencies, String version, String description) {

  public ModuleMetadata {
    Objects.requireNonNull(id, "id");
    if (id.isBlank()) {
      throw new IllegalArgumentException("id must not be blank");
    }
    dependencies = Set.copyOf(dependencies == null ? Set.of() : dependencies);
    version = (version == null || version.isBlank()) ? "0.0.0" : version;
    description = description == null ? "" : description;
  }

  public static ModuleMetadata minimal(String id) {
    return new ModuleMetadata(id, Set.of(), "0.1.0", "");
  }
}
