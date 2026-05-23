package com.hanielcota.essentials.config;

import com.hanielcota.essentials.util.Log;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ConfigService} using {@link YamlConfigHandle} as the backing storage.
 *
 * <p>Responsibility for loading individual configurations is delegated to YamlConfigHandle,
 * adhering to Single Responsibility Principle (SRP).
 */
@RequiredArgsConstructor
public final class YamlConfigService implements ConfigService {

  private static final Log LOG = Log.of(YamlConfigService.class);

  private final Path baseDir;
  private final Map<String, YamlConfigHandle<?>> handles = new ConcurrentHashMap<>();
  private final CopyOnWriteArrayList<Runnable> reloadCallbacks = new CopyOnWriteArrayList<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> ConfigHandle<T> load(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults) {
    var handle =
        this.handles.computeIfAbsent(
            name,
            key -> {
              var newHandle = new YamlConfigHandle<>(this.baseDir, key, type, defaults);
              newHandle.refresh();
              return newHandle;
            });

    var existingType = handle.type();
    if (!existingType.equals(type)) {
      var configName = handle.name();
      var existingTypeName = existingType.getName();
      var requestedTypeName = type.getName();

      throw new IllegalStateException(
          "Config "
              + configName
              + " already loaded with type "
              + existingTypeName
              + ", refused to re-load as "
              + requestedTypeName);
    }

    return (ConfigHandle<T>) handle;
  }

  @Override
  public ReloadReport reloadAll() {
    var failures = new LinkedHashMap<String, String>();

    var configHandles = this.handles.values();
    for (var handle : configHandles) {
      try {
        handle.refresh();
      } catch (RuntimeException e) {
        var exceptionMessage = e.getMessage();
        var errorMessage = (exceptionMessage != null) ? exceptionMessage : e.toString();

        failures.put(handle.name(), errorMessage);
      }
    }

    for (var callback : this.reloadCallbacks) {
      try {
        callback.run();
      } catch (RuntimeException e) {
        LOG.warn(e, "Reload callback failed");
      }
    }

    var totalHandlesCount = this.handles.size();
    return new ReloadReport(totalHandlesCount, failures);
  }

  @Override
  public AutoCloseable onReload(@NonNull Runnable callback) {
    this.reloadCallbacks.add(callback);
    return () -> this.reloadCallbacks.remove(callback);
  }
}
