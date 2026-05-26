package com.hanielcota.essentials.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.exception.ModuleLoadException;
import com.hanielcota.essentials.module.discovery.ModuleDependencyResolver;
import com.hanielcota.essentials.module.environment.ModuleContext;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class ModuleDependencyResolverTest {

  @Test
  void emptyInputReturnsEmptyList() {
    var resolved = ModuleDependencyResolver.resolve(List.of());

    assertTrue(resolved.isEmpty());
  }

  @Test
  void modulesWithoutDependenciesKeepInsertionOrder() {
    var first = stub("a");
    var second = stub("b");
    var third = stub("c");

    var resolved = ModuleDependencyResolver.resolve(List.of(first, second, third));

    assertEquals(List.of(first, second, third), resolved);
  }

  @Test
  void dependencyComesBeforeDependent() {
    var dep = stub("dep");
    var consumer = stub("consumer", "dep");

    // Pass consumer first to prove the resolver reorders by dependency, not by input order.
    var resolved = ModuleDependencyResolver.resolve(List.of(consumer, dep));

    assertEquals(List.of(dep, consumer), resolved);
  }

  @Test
  void linearChainIsTopologicallySorted() {
    var a = stub("a");
    var b = stub("b", "a");
    var c = stub("c", "b");

    var resolved = ModuleDependencyResolver.resolve(List.of(c, b, a));

    assertEquals(List.of(a, b, c), resolved);
  }

  @Test
  void missingDependencyThrowsModuleLoadException() {
    var orphan = stub("orphan", "missing");

    var exception =
        assertThrows(
            ModuleLoadException.class, () -> ModuleDependencyResolver.resolve(List.of(orphan)));
    assertTrue(exception.getMessage().contains("missing"));
  }

  @Test
  void cycleThrowsWithBothMembersListed() {
    var first = stub("first", "second");
    var second = stub("second", "first");

    var exception =
        assertThrows(
            ModuleLoadException.class,
            () -> ModuleDependencyResolver.resolve(List.of(first, second)));
    var message = exception.getMessage();
    assertTrue(message.contains("first") && message.contains("second"), message);
  }

  private static Module stub(@NonNull String id, @NonNull String... deps) {
    var metadata = new ModuleMetadata(id, Set.of(deps), "1.0", "");
    return new StubModule(metadata);
  }

  private record StubModule(ModuleMetadata metadata) implements Module {
    @Override
    public void enable(@NonNull ModuleContext context) {}

    @Override
    public void disable() {}
  }
}
