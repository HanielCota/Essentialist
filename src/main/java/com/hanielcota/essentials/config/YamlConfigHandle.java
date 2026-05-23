package com.hanielcota.essentials.config;

import com.hanielcota.essentials.exception.ConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * Implementation of {@link ConfigHandle} backed by a YAML file on disk.
 *
 * <p>Extracted to a package-private top-level class to adhere to the Single Responsibility
 * Principle (SRP) and simplify YamlConfigService.
 */
final class YamlConfigHandle<T> implements ConfigHandle<T> {

  private final Path baseDir;
  private final String name;
  private final Class<T> type;
  private final Supplier<T> defaults;
  private final AtomicReference<T> ref = new AtomicReference<>();

  YamlConfigHandle(Path baseDir, String name, Class<T> type, Supplier<T> defaults) {
    this.baseDir = baseDir;
    this.name = name;
    this.type = type;
    this.defaults = defaults;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public T value() {
    return ref.get();
  }

  @Override
  public void reload() {
    refresh();
  }

  void refresh() {
    ref.set(readFromDisk());
  }

  Class<T> type() {
    return type;
  }

  private T readFromDisk() {
    var file = baseDir.resolve(name + ".yml");
    ensureParent(file);

    var loader =
        YamlConfigurationLoader.builder().path(file).nodeStyle(NodeStyle.BLOCK).indent(2).build();

    try {
      var node = loader.load();
      var defaultsNode = CommentedConfigurationNode.root(node.options());
      defaultsNode.set(type, defaults.get());

      var sizeBefore = node.childrenMap().size();
      node.mergeFrom(defaultsNode);

      var dirty = node.empty() || node.childrenMap().size() != sizeBefore;
      var value = node.get(type);

      if (value == null) {
        value = defaults.get();
        dirty = true;
      }

      if (dirty) {
        loader.save(node);
      }

      return value;
    } catch (ConfigurateException e) {
      throw new ConfigurationException("Failed to load config: " + name, e);
    }
  }

  private void ensureParent(Path file) {
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
}
