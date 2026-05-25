package com.hanielcota.essentials.config;

import com.hanielcota.essentials.util.Log;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
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
    Function<String, YamlConfigHandle<?>> factory = key -> createHandle(key, type, defaults);
    var handle = this.handles.computeIfAbsent(name, factory);

    var existingType = handle.type();
    if (existingType != type) {
      var configName = handle.name();
      var existingTypeName = existingType.getName();
      var requestedTypeName = type.getName();

      var errorMessage =
          "Config "
              + configName
              + " already loaded with type "
              + existingTypeName
              + ", refused to re-load as "
              + requestedTypeName;

      throw new IllegalStateException(errorMessage);
    }

    return (ConfigHandle<T>) handle;
  }

  private <T> YamlConfigHandle<T> createHandle(
      @NonNull String key, @NonNull Class<T> type, @NonNull Supplier<T> defaults) {
    var newHandle = new YamlConfigHandle<>(this.baseDir, key, type, defaults);
    newHandle.refresh();
    return newHandle;
  }

  @Override
  public ReloadReport reloadAll() {
    var failures = new LinkedHashMap<String, String>();

    // Snapshot before iterating: handles is concurrent, and reporting `total` from a post-iteration
    // size() would skew the count if another thread loads a new config mid-reload.
    var handleValues = this.handles.values();
    var snapshot = List.copyOf(handleValues);

    for (var handle : snapshot) {
      try {
        handle.refresh();
      } catch (RuntimeException e) {
        var exceptionMessage = e.getMessage();

        String errorMessage;
        if (exceptionMessage != null) {
          errorMessage = exceptionMessage;
        } else {
          errorMessage = e.toString();
        }

        var handleName = handle.name();
        failures.put(handleName, errorMessage);
      }
    }

    for (var callback : this.reloadCallbacks) {
      try {
        callback.run();
      } catch (RuntimeException e) {
        LOG.warn(e, "Reload callback failed");
      }
    }

    return new ReloadReport(snapshot.size(), failures);
  }

  @Override
  public AutoCloseable onReload(@NonNull Runnable callback) {
    this.reloadCallbacks.add(callback);
    return () -> this.reloadCallbacks.remove(callback);
  }
}
