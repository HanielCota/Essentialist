package com.hanielcota.essentials.module;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ModuleSettings(
    @Comment("Module enable switches. Missing modules default to enabled.")
        Map<String, Boolean> modules) {

  public ModuleSettings {
    modules = copyOf(modules);
  }

  public static ModuleSettings defaults() {
    return new ModuleSettings(Map.of());
  }

  public static ModuleSettings forModules(@NonNull Iterable<Module> modules) {
    var switches = new LinkedHashMap<String, Boolean>();
    for (var module : modules) {
      switches.put(module.id(), true);
    }

    return new ModuleSettings(switches);
  }

  private static Map<String, Boolean> copyOf(Map<String, Boolean> source) {
    if (source == null || source.isEmpty()) {
      return Map.of();
    }

    var copy = new LinkedHashMap<>(source);
    return Collections.unmodifiableMap(copy);
  }

  public boolean enabled(@NonNull String moduleId) {
    return this.modules.getOrDefault(moduleId, true);
  }
}
