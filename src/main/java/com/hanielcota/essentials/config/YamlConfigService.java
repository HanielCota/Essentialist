package com.hanielcota.essentials.config;

import com.hanielcota.essentials.util.Log;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Implementation of {@link ConfigService} using {@link YamlConfigHandle} as the backing storage.
 *
 * <p>Responsibility for loading individual configurations is delegated to YamlConfigHandle,
 * adhering to Single Responsibility Principle (SRP).
 */
public final class YamlConfigService implements ConfigService {

  private static final Log LOG = Log.of(YamlConfigService.class);

  private final Path baseDir;
  private final Map<String, YamlConfigHandle<?>> handles = new ConcurrentHashMap<>();
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
              var newHandle = new YamlConfigHandle<>(baseDir, key, type, defaults);
              newHandle.refresh();
              return newHandle;
            });

    if (!handle.type().equals(type)) {
      throw new IllegalStateException(
          "Config "
              + name
              + " already loaded with type "
              + handle.type().getName()
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
}
