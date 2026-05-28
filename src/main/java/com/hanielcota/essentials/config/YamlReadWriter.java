package com.hanielcota.essentials.config;

import com.hanielcota.essentials.exception.ConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import lombok.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * Reads a Configurate YAML file, merges missing keys from a defaults instance, and writes the
 * merged document back to disk. Used by both {@link YamlConfigHandle} (per-module configs) and the
 * bootstrap settings loader, so the read+merge+save flow lives in exactly one place.
 *
 * <p>The file is always rewritten after a load. Detecting whether {@code mergeFrom} introduced new
 * keys reliably requires deep node comparison; comparing top-level child counts misses any nested
 * key added inside an existing section, leaving the file stale forever. The disk IO is a one-time
 * cost at config load, so paying it unconditionally is cheaper than the bug it prevents.
 */
public final class YamlReadWriter {

  private YamlReadWriter() {}

  public static <T> T readMerging(
      @NonNull Path file, @NonNull Class<T> type, @NonNull Supplier<T> defaults) {
    ensureParent(file);

    var loader = loaderFor(file);
    try {
      var node = loader.load();
      var options = node.options();
      var defaultsNode = CommentedConfigurationNode.root(options);

      var defaultInstance = defaults.get();
      defaultsNode.set(type, defaultInstance);
      node.mergeFrom(defaultsNode);

      var value = node.get(type);
      if (value == null) {
        value = defaultInstance;
      }

      loader.save(node);
      return value;
    } catch (ConfigurateException e) {
      throw new ConfigurationException("Failed to load YAML: " + file, e);
    }
  }

  private static YamlConfigurationLoader loaderFor(@NonNull Path file) {
    var builder = YamlConfigurationLoader.builder();
    builder.path(file);
    builder.nodeStyle(NodeStyle.BLOCK);
    builder.indent(2);
    return builder.build();
  }

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
}
