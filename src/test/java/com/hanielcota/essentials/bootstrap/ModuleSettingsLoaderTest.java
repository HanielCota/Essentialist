package com.hanielcota.essentials.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleContext;
import com.hanielcota.essentials.module.ModuleMetadata;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModuleSettingsLoaderTest {

  @TempDir private Path tempDir;

  @Test
  void createsModuleSettingsWithDiscoveredModulesEnabled() throws Exception {
    var loader = new ModuleSettingsLoader(this.tempDir);
    var modules = List.of(module("homes"), module("spawn"));

    var settings = loader.load(modules);

    assertTrue(settings.enabled("homes"));
    assertTrue(settings.enabled("spawn"));

    var file = this.tempDir.resolve("modules.yml");
    var yaml = Files.readString(file);

    assertTrue(yaml.contains("homes: true"));
    assertTrue(yaml.contains("spawn: true"));
  }

  @Test
  void keepsExistingDisabledModuleWhenNewDefaultsAreMerged() throws Exception {
    var file = this.tempDir.resolve("modules.yml");
    Files.writeString(file, "modules:\n  homes: false\n");

    var loader = new ModuleSettingsLoader(this.tempDir);
    var modules = List.of(module("homes"), module("spawn"));

    var settings = loader.load(modules);

    assertFalse(settings.enabled("homes"));
    assertTrue(settings.enabled("spawn"));
  }

  private static Module module(String id) {
    var metadata = ModuleMetadata.minimal(id);
    return new TestModule(metadata);
  }

  private record TestModule(ModuleMetadata metadata) implements Module {

    @Override
    public void enable(ModuleContext context) {}

    @Override
    public void disable() {}
  }
}
