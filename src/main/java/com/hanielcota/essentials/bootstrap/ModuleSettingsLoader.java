package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.exception.ConfigurationException;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.discovery.ModuleSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@RequiredArgsConstructor
final class ModuleSettingsLoader {

  private final @NonNull Path dataFolder;

  private static void ensureParent(@NonNull Path file) {
    var parent = file.getParent();
    if (parent == null) {
      return;
    }

    try {
      Files.createDirectories(parent);
    } catch (IOException e) {
      throw new ConfigurationException("Failed to create config directory: " + parent, e);
    }
  }

  ModuleSettings load(@NonNull Collection<Module> modules) {
    var file = this.dataFolder.resolve("modules.yml");
    ensureParent(file);

    var builder = YamlConfigurationLoader.builder();
    builder.path(file);
    builder.nodeStyle(NodeStyle.BLOCK);
    builder.indent(2);
    var loader = builder.build();

    try {
      var node = loader.load();
      var options = node.options();
      var defaultsNode = CommentedConfigurationNode.root(options);
      var defaultSettings = ModuleSettings.forModules(modules);

      defaultsNode.set(ModuleSettings.class, defaultSettings);
      node.mergeFrom(defaultsNode);

      var settings = node.get(ModuleSettings.class);
      if (settings == null) {
        settings = defaultSettings;
      }

      loader.save(node);
      return settings;
    } catch (ConfigurateException e) {
      throw new ConfigurationException("Failed to load module settings", e);
    }
  }
}
