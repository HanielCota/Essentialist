package com.hanielcota.essentials.module;

import java.util.Set;
import lombok.NonNull;

public record ModuleMetadata(
    String id, Set<String> dependencies, String version, String description) {

  public ModuleMetadata {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id must not be blank");
    }

    dependencies = (dependencies == null) ? Set.of() : Set.copyOf(dependencies);

    if (version == null || version.isBlank()) {
      version = "0.0.0";
    }

    if (description == null) {
      description = "";
    }
  }

  public static ModuleMetadata minimal(@NonNull String id) {
    var defaultDependencies = Set.<String>of();
    return new ModuleMetadata(id, defaultDependencies, "0.1.0", "");
  }
}
