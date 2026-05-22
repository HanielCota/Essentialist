package com.hanielcota.essentials.config;

import com.hanielcota.essentials.exception.ConfigurationException;
import com.hanielcota.essentials.util.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class YamlConfigService implements ConfigService {

  private static final Log LOG = Log.of(YamlConfigService.class);

  private final Path baseDir;
  private final Map<String, Handle<?>> handles = new ConcurrentHashMap<>();
  private final CopyOnWriteArrayList<Runnable> reloadCallbacks = new CopyOnWriteArrayList<>();

  public YamlConfigService(Path baseDir) {
    this.baseDir = Objects.requireNonNull(baseDir, "baseDir");
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> ConfigHandle<T> load(String name, Class<T> type, Supplier<T> defaults) {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(defaults, "defaults");

    var handle =
        handles.computeIfAbsent(
            name,
            key -> {
              var newHandle = new Handle<>(key, type, defaults);
              newHandle.refresh();
              return newHandle;
            });

    if (!handle.type.equals(type)) {
      throw new IllegalStateException(
          "Config "
              + name
              + " already loaded with type "
              + handle.type.getName()
              + ", refused to re-load as "
              + type.getName());
    }

    return (ConfigHandle<T>) handle;
  }

  @Override
  public ReloadReport reloadAll() {
    var failures = new LinkedHashMap<String, String>();

    for (var handle : handles.values()) {
      try {
        handle.refresh();
      } catch (RuntimeException e) {
        var errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
        failures.put(handle.name(), errorMessage);
      }
    }

    for (var callback : reloadCallbacks) {
      try {
        callback.run();
      } catch (RuntimeException e) {
        LOG.warn(e, "Reload callback failed");
      }
    }

    return new ReloadReport(handles.size(), failures);
  }

  @Override
  public AutoCloseable onReload(Runnable callback) {
    Objects.requireNonNull(callback, "callback");

    reloadCallbacks.add(callback);
    return () -> reloadCallbacks.remove(callback);
  }

  private final class Handle<T> implements ConfigHandle<T> {

    private final String name;
    private final Class<T> type;
    private final Supplier<T> defaults;
    private final AtomicReference<T> ref = new AtomicReference<>();

    private Handle(String name, Class<T> type, Supplier<T> defaults) {
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

    private void refresh() {
      ref.set(readFromDisk());
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

        int sizeBefore = node.childrenMap().size();
        node.mergeFrom(defaultsNode);

        boolean dirty = node.empty() || node.childrenMap().size() != sizeBefore;
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
}
