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
import lombok.NonNull;

public final class ModuleManager {

  private static final Log LOG = Log.of(ModuleManager.class);

  private final Map<String, Module> registered = new LinkedHashMap<>();
  private final Map<String, ModuleState> states = new LinkedHashMap<>();
  private List<Module> enableOrder = List.of();

  public void register(@NonNull Module module) {
    var moduleId = module.id();

    if (this.registered.putIfAbsent(moduleId, module) == null) {
      this.states.put(moduleId, ModuleState.REGISTERED);
      return;
    }
    LOG.warn("Duplicate module id: {} — keeping the first registration", moduleId);
  }

  public void enableAll(@NonNull ModuleContext context) {
    this.enableOrder = resolveLoadOrder();

    var size = this.enableOrder.size();
    var succeeded = new ArrayList<Module>(size);

    for (var module : this.enableOrder) {
      var moduleId = module.id();
      try {
        module.enable(context);
        this.states.put(moduleId, ModuleState.ENABLED);
        succeeded.add(module);
      } catch (RuntimeException e) {
        this.states.put(moduleId, ModuleState.FAILED);
        rollback(succeeded);
        throw new ModuleLoadException(moduleId, "enable() failed", e);
      }
    }
  }

  public void disableAll() {
    for (var module : this.enableOrder.reversed()) {
      var moduleId = module.id();
      var currentState = this.states.get(moduleId);

      if (currentState != ModuleState.ENABLED) {
        continue;
      }

      try {
        module.disable();
        this.states.put(moduleId, ModuleState.DISABLED);
      } catch (RuntimeException e) {
        this.states.put(moduleId, ModuleState.FAILED);
        LOG.error(e, "Module disable failed: {}", moduleId);
      }
    }
  }

  public Collection<Module> all() {
    var values = this.registered.values();
    return List.copyOf(values);
  }

  private void rollback(@NonNull List<Module> succeeded) {
    var lastIndex = succeeded.size() - 1;

    for (var i = lastIndex; i >= 0; i--) {
      var module = succeeded.get(i);
      var moduleId = module.id();

      try {
        module.disable();
        this.states.put(moduleId, ModuleState.DISABLED);
      } catch (RuntimeException e) {
        this.states.put(moduleId, ModuleState.FAILED);
        LOG.error(e, "Rollback disable failed: {}", moduleId);
      }
    }
  }

  private List<Module> resolveLoadOrder() {
    var inDegree = new LinkedHashMap<String, Integer>();
    var dependents = new HashMap<String, List<String>>();

    for (var module : this.registered.values()) {
      var moduleId = module.id();
      inDegree.put(moduleId, 0);
      dependents.put(moduleId, new ArrayList<>());
    }

    for (var module : this.registered.values()) {
      var moduleId = module.id();
      var metadata = module.metadata();
      var dependencies = metadata.dependencies();

      for (var dep : dependencies) {
        if (!this.registered.containsKey(dep)) {
          throw new ModuleLoadException(moduleId, "missing dependency: " + dep);
        }

        var dependentList = dependents.get(dep);
        dependentList.add(moduleId);
        inDegree.merge(moduleId, 1, Integer::sum);
      }
    }

    var ready = new ArrayDeque<String>();
    for (var entry : inDegree.entrySet()) {
      if (entry.getValue() == 0) {
        ready.add(entry.getKey());
      }
    }

    var totalRegisteredSize = this.registered.size();
    var ordered = new ArrayList<Module>(totalRegisteredSize);

    while (!ready.isEmpty()) {
      var id = ready.poll();
      var currentModule = this.registered.get(id);
      ordered.add(currentModule);

      var nextDependents = dependents.get(id);
      for (var next : nextDependents) {
        var remaining = inDegree.merge(next, -1, Integer::sum);
        if (remaining == 0) {
          ready.add(next);
        }
      }
    }

    if (ordered.size() != totalRegisteredSize) {
      var stuck = new ArrayList<String>();
      for (var entry : inDegree.entrySet()) {
        if (entry.getValue() > 0) {
          stuck.add(entry.getKey());
        }
      }

      var cycleIds = String.join(",", stuck);
      throw new ModuleLoadException(cycleIds, "dependency cycle detected");
    }

    return List.copyOf(ordered);
  }
}
