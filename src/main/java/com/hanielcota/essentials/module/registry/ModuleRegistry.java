package com.hanielcota.essentials.module.registry;

import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.shared.Log;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/**
 * Holds the registered modules and their lifecycle states. Pure state holder — no lifecycle
 * orchestration, no dependency resolution, no logging beyond duplicate-id warnings.
 */
public final class ModuleRegistry {

  private static final Log LOG = Log.of(ModuleRegistry.class);

  private final Map<String, Module> registered = new LinkedHashMap<>();
  private final Map<String, ModuleState> states = new LinkedHashMap<>();

  public void register(@NonNull Module module) {
    var moduleId = module.id();

    if (this.registered.putIfAbsent(moduleId, module) == null) {
      this.states.put(moduleId, ModuleState.REGISTERED);
      return;
    }

    LOG.warn("Duplicate module id: {} — keeping the first registration", moduleId);
  }

  public Collection<Module> all() {
    var values = this.registered.values();

    return List.copyOf(values);
  }

  public boolean contains(@NonNull String moduleId) {
    return this.registered.containsKey(moduleId);
  }

  public Module get(@NonNull String moduleId) {
    return this.registered.get(moduleId);
  }

  public ModuleState stateOf(@NonNull String moduleId) {
    return this.states.get(moduleId);
  }

  public void markState(@NonNull String moduleId, @NonNull ModuleState state) {
    this.states.put(moduleId, state);
  }
}
