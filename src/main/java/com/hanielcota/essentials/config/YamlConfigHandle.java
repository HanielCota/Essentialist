package com.hanielcota.essentials.config;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ConfigHandle} backed by a YAML file on disk. The read+merge+save flow
 * lives in {@link YamlReadWriter} so this class is just the {@code AtomicReference} cache and the
 * path-naming convention.
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

  void refresh() {
    var file = this.baseDir.resolve(this.name + ".yml");
    var updatedValue = YamlReadWriter.readMerging(file, this.type, this.defaults);
    this.ref.set(updatedValue);
  }

  Class<T> type() {
    return this.type;
  }
}
