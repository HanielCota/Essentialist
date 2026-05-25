package com.hanielcota.essentials.config;

import java.util.function.Consumer;
import lombok.NonNull;

/**
 * Snapshot accessor for a single named config file.
 *
 * <p>{@link #value()} returns the current snapshot atomically — callers should read it once per
 * operation and pass the snapshot down so a concurrent {@link #reload()} cannot tear a single
 * decision across pre- and post-reload state. Long-running listeners and timers that hold
 * references to config fields must subscribe to {@link #onReload(Consumer)} so their derived state
 * (timers, caches, executors sized from config) is rebuilt when the file changes.
 */
public interface ConfigHandle<T> {

  String name();

  T value();

  void reload();

  /**
   * Invokes {@code listener} with the freshly reloaded value every time this handle is reloaded.
   * Returns an {@link AutoCloseable} that unsubscribes; modules should pass the returned closeable
   * to {@code registerCloseable} so the subscription is dropped on disable.
   */
  AutoCloseable onReload(@NonNull Consumer<T> listener);
}
