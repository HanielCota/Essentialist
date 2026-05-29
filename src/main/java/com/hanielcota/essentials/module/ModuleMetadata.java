package com.hanielcota.essentials.module;

import java.util.Set;
import lombok.NonNull;

/**
 * Immutable identity and dependency declaration for a {@link Module}.
 *
 * <p>The compact constructor normalises its inputs rather than rejecting them: {@code dependencies}
 * is defensively copied (and becomes an empty immutable set when {@code null}), a blank or {@code
 * null} {@code version} falls back to {@code "0.0.0"}, and a {@code null} {@code description}
 * becomes {@code ""}. Only a blank {@code id} is rejected.
 *
 * @param id stable module identifier; referenced by other modules' {@code dependencies}. Must not
 *     be blank.
 * @param dependencies ids of modules that must be enabled before this one
 * @param version human-readable version string
 * @param description short human-readable description
 * @throws IllegalArgumentException if {@code id} is {@code null} or blank
 */
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

  /** Metadata for a module that declares no dependencies, at version {@code 0.1.0}. */
  public static ModuleMetadata minimal(@NonNull String id) {
    var defaultDependencies = Set.<String>of();
    return new ModuleMetadata(id, defaultDependencies, "0.1.0", "");
  }
}
