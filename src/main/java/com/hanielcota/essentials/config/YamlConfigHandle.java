package com.hanielcota.essentials.config;

import com.hanielcota.essentials.exception.ConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
final class YamlConfigHandle<T> implements ConfigHandle<T> {

  private final @NonNull Path baseDir;
  private final @NonNull String name;
  private final @NonNull Class<T> type;
  private final @NonNull Supplier<T> defaults;

  private final AtomicReference<T> ref = new AtomicReference<>();

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public T value() {
    return this.ref.get();
  }

  @Override
  public void reload() {
    refresh();
  }

  void refresh() {
    var updatedValue = readFromDisk();
    this.ref.set(updatedValue);
  }

  Class<T> type() {
    return this.type;
  }

  private T readFromDisk() {
    var fileName = this.name + ".yml";
    var file = this.baseDir.resolve(fileName);
    ensureParent(file);

    var loader =
        YamlConfigurationLoader.builder().path(file).nodeStyle(NodeStyle.BLOCK).indent(2).build();

    try {
      var node = loader.load();
      var options = node.options();
      var defaultsNode = CommentedConfigurationNode.root(options);

      var defaultInstance = this.defaults.get();
      defaultsNode.set(this.type, defaultInstance);

      node.mergeFrom(defaultsNode);

      var value = node.get(this.type);
      if (value == null) {
        value = defaultInstance;
      }

      // Always rewrite. Detecting whether mergeFrom introduced new keys reliably
      // requires deep node comparison; comparing top-level childrenMap().size()
      // (the previous heuristic) missed any nested key added inside an existing
      // section, leaving the file stale forever. The file IO is a one-time cost
      // at config load, so paying it unconditionally is cheaper than the
      // alternative bug.
      loader.save(node);

      return value;

    } catch (ConfigurateException e) {
      throw new ConfigurationException("Failed to load config: " + this.name, e);
    }
  }

  private void ensureParent(@NonNull Path file) {
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
