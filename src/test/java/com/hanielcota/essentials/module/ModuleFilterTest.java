package com.hanielcota.essentials.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hanielcota.essentials.exception.ModuleLoadException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleFilterTest {

  @Test
  void keepsModulesEnabledByDefault() {
    var settings = ModuleSettings.defaults();
    var modules = List.of(module("homes"), module("spawn"));

    var enabled = ModuleFilter.enabled(modules, settings);

    assertEquals(List.of("homes", "spawn"), idsOf(enabled));
  }

  @Test
  void removesExplicitlyDisabledModules() {
    var settings = new ModuleSettings(Map.of("homes", false));
    var modules = List.of(module("homes"), module("spawn"));

    var enabled = ModuleFilter.enabled(modules, settings);

    assertEquals(List.of("spawn"), idsOf(enabled));
  }

  @Test
  void rejectsEnabledModuleWhenDependencyIsDisabled() {
    var settings = new ModuleSettings(Map.of("teleport", false));
    var modules = List.of(module("teleport"), module("spawn", "teleport"));

    var error =
        assertThrows(ModuleLoadException.class, () -> ModuleFilter.enabled(modules, settings));

    assertEquals("[spawn] missing dependency: teleport", error.getMessage());
  }

  private static List<String> idsOf(List<Module> modules) {
    return modules.stream().map(Module::id).toList();
  }

  private static Module module(String id, String... dependencies) {
    var metadata = new ModuleMetadata(id, Set.of(dependencies), "test", "");
    return new TestModule(metadata);
  }

  private record TestModule(ModuleMetadata metadata) implements Module {

    @Override
    public void enable(ModuleContext context) {
      // no-op — test double, filter only inspects metadata
    }

    @Override
    public void disable() {
      // no-op — test double
    }
  }
}
