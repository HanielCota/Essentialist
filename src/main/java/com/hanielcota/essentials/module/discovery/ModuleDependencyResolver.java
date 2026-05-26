package com.hanielcota.essentials.module.discovery;

import com.hanielcota.essentials.exception.ModuleLoadException;
import com.hanielcota.essentials.module.Module;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Resolves module enable order via Kahn's algorithm over the dependency graph. Throws {@link
 * ModuleLoadException} when a declared dependency is missing or when the graph has a cycle.
 *
 * <p>Stateless — every call recomputes from scratch over the supplied collection.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModuleDependencyResolver {

  public static List<Module> resolve(@NonNull Collection<Module> modules) {
    var indexed = new LinkedHashMap<String, Module>();
    for (var module : modules) {
      indexed.put(module.id(), module);
    }

    var inDegree = new LinkedHashMap<String, Integer>();
    var dependents = new HashMap<String, List<String>>();

    for (var module : modules) {
      var moduleId = module.id();
      inDegree.put(moduleId, 0);
      dependents.put(moduleId, new ArrayList<>());
    }

    populateGraph(modules, indexed, inDegree, dependents);

    var ready = readyQueue(inDegree);
    var ordered = drain(ready, indexed, dependents, inDegree, modules.size());

    if (ordered.size() != modules.size()) {
      var stuck = stuckIds(inDegree);
      var cycleIds = String.join(",", stuck);
      throw new ModuleLoadException(cycleIds, "dependency cycle detected");
    }

    return List.copyOf(ordered);
  }

  private static void populateGraph(
      @NonNull Collection<Module> modules,
      @NonNull LinkedHashMap<String, Module> indexed,
      @NonNull LinkedHashMap<String, Integer> inDegree,
      @NonNull HashMap<String, List<String>> dependents) {
    for (var module : modules) {
      var moduleId = module.id();
      var metadata = module.metadata();
      var dependencies = metadata.dependencies();

      for (var dep : dependencies) {
        if (!indexed.containsKey(dep)) {
          throw new ModuleLoadException(moduleId, "missing dependency: " + dep);
        }

        var dependentList = dependents.get(dep);
        dependentList.add(moduleId);
        inDegree.merge(moduleId, 1, Integer::sum);
      }
    }
  }

  private static ArrayDeque<String> readyQueue(@NonNull LinkedHashMap<String, Integer> inDegree) {
    var ready = new ArrayDeque<String>();
    for (var entry : inDegree.entrySet()) {
      if (entry.getValue() == 0) {
        ready.add(entry.getKey());
      }
    }

    return ready;
  }

  private static List<Module> drain(
      @NonNull ArrayDeque<String> ready,
      @NonNull LinkedHashMap<String, Module> indexed,
      @NonNull HashMap<String, List<String>> dependents,
      @NonNull LinkedHashMap<String, Integer> inDegree,
      int expectedSize) {
    var ordered = new ArrayList<Module>(expectedSize);

    while (!ready.isEmpty()) {
      var id = ready.poll();
      var currentModule = indexed.get(id);
      ordered.add(currentModule);

      var nextDependents = dependents.get(id);
      for (var next : nextDependents) {
        var remaining = inDegree.merge(next, -1, Integer::sum);
        if (remaining == 0) {
          ready.add(next);
        }
      }
    }

    return ordered;
  }

  private static List<String> stuckIds(@NonNull LinkedHashMap<String, Integer> inDegree) {
    var stuck = new ArrayList<String>();
    for (var entry : inDegree.entrySet()) {
      if (entry.getValue() > 0) {
        stuck.add(entry.getKey());
      }
    }

    return stuck;
  }
}
