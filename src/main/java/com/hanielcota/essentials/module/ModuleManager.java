package com.hanielcota.essentials.module;

import com.hanielcota.essentials.exception.ModuleLoadException;
import com.hanielcota.essentials.util.Log;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ModuleManager {

  private static final Log LOG = Log.of(ModuleManager.class);

  private final Map<String, Module> registered = new LinkedHashMap<>();
  private final Map<String, ModuleState> states = new LinkedHashMap<>();
  private List<Module> enableOrder = List.of();

  public void register(Module module) {
    Objects.requireNonNull(module, "module");
    if (registered.putIfAbsent(module.id(), module) == null) {
      states.put(module.id(), ModuleState.REGISTERED);
    }
  }

  public void enableAll(ModuleContext context) {
    Objects.requireNonNull(context, "context");
    enableOrder = resolveLoadOrder();

    var succeeded = new ArrayList<Module>(enableOrder.size());
    for (Module module : enableOrder) {
      try {
        module.enable(context);
        states.put(module.id(), ModuleState.ENABLED);
        succeeded.add(module);
      } catch (RuntimeException e) {
        states.put(module.id(), ModuleState.FAILED);
        rollback(succeeded);
        throw new ModuleLoadException(module.id(), "enable() failed", e);
      }
    }
  }

  public void disableAll() {
    var reversed = new ArrayList<Module>(enableOrder);
    for (Module module : reversed.reversed()) {
      if (states.get(module.id()) != ModuleState.ENABLED) {
        continue;
      }
      try {
        module.disable();
        states.put(module.id(), ModuleState.DISABLED);
      } catch (RuntimeException e) {
        states.put(module.id(), ModuleState.FAILED);
        LOG.error(e, "Module disable failed: {}", module.id());
      }
    }
  }

  public Optional<Module> get(String id) {
    return Optional.ofNullable(registered.get(Objects.requireNonNull(id, "id")));
  }

  public ModuleState stateOf(String id) {
    return states.getOrDefault(Objects.requireNonNull(id, "id"), ModuleState.DISABLED);
  }

  public Collection<Module> all() {
    return List.copyOf(registered.values());
  }

  public Map<String, ModuleState> snapshot() {
    return Map.copyOf(states);
  }

  private void rollback(List<Module> succeeded) {
    for (int i = succeeded.size() - 1; i >= 0; i--) {
      var module = succeeded.get(i);
      try {
        module.disable();
        states.put(module.id(), ModuleState.DISABLED);
      } catch (RuntimeException e) {
        states.put(module.id(), ModuleState.FAILED);
        LOG.error(e, "Rollback disable failed: {}", module.id());
      }
    }
  }

  private List<Module> resolveLoadOrder() {
    var inDegree = new LinkedHashMap<String, Integer>();
    var dependents = new HashMap<String, List<String>>();

    for (Module module : registered.values()) {
      inDegree.put(module.id(), 0);
      dependents.put(module.id(), new ArrayList<>());
    }

    for (Module module : registered.values()) {
      for (String dep : module.metadata().dependencies()) {
        if (!registered.containsKey(dep)) {
          throw new ModuleLoadException(module.id(), "missing dependency: " + dep);
        }
        dependents.get(dep).add(module.id());
        inDegree.merge(module.id(), 1, Integer::sum);
      }
    }

    var ready = new ArrayDeque<String>();
    for (var entry : inDegree.entrySet()) {
      if (entry.getValue() == 0) {
        ready.add(entry.getKey());
      }
    }

    var ordered = new ArrayList<Module>(registered.size());
    while (!ready.isEmpty()) {
      String id = ready.poll();
      ordered.add(registered.get(id));
      for (String next : dependents.get(id)) {
        int remaining = inDegree.merge(next, -1, Integer::sum);
        if (remaining == 0) {
          ready.add(next);
        }
      }
    }

    if (ordered.size() != registered.size()) {
      var stuck =
          inDegree.entrySet().stream()
              .filter(e -> e.getValue() > 0)
              .map(Map.Entry::getKey)
              .toList();
      throw new ModuleLoadException(String.join(",", stuck), "dependency cycle detected");
    }

    return List.copyOf(ordered);
  }
}
