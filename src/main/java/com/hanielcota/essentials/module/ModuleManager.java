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

public final class ModuleManager {

  private static final Log LOG = Log.of(ModuleManager.class);

  private final Map<String, Module> registered = new LinkedHashMap<>();
  private final Map<String, ModuleState> states = new LinkedHashMap<>();
  private List<Module> enableOrder = List.of();

  public void register(Module module) {
    if (registered.putIfAbsent(module.id(), module) == null) {
      states.put(module.id(), ModuleState.REGISTERED);
    }
  }

  public void enableAll(ModuleContext context) {
    enableOrder = resolveLoadOrder();

    var succeeded = new ArrayList<Module>(enableOrder.size());
    for (var module : enableOrder) {
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
    for (var module : reversed.reversed()) {
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

  public Collection<Module> all() {
    return List.copyOf(registered.values());
  }

  private void rollback(List<Module> succeeded) {
    for (var i = succeeded.size() - 1; i >= 0; i--) {
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

    for (var module : registered.values()) {
      inDegree.put(module.id(), 0);
      dependents.put(module.id(), new ArrayList<>());
    }

    for (var module : registered.values()) {
      for (var dep : module.metadata().dependencies()) {
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
      var id = ready.poll();
      ordered.add(registered.get(id));
      for (var next : dependents.get(id)) {
        var remaining = inDegree.merge(next, -1, Integer::sum);
        if (remaining == 0) {
          ready.add(next);
        }
      }
    }

    if (ordered.size() != registered.size()) {
      var stuck = new ArrayList<String>();
      for (var entry : inDegree.entrySet()) {
        if (entry.getValue() > 0) stuck.add(entry.getKey());
      }
      throw new ModuleLoadException(String.join(",", stuck), "dependency cycle detected");
    }

    return List.copyOf(ordered);
  }
}
