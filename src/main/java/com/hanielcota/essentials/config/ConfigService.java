package com.hanielcota.essentials.config;

import java.util.function.Supplier;
import lombok.NonNull;

/** Loads named config files and exposes live {@link ConfigHandle} snapshots over them. */
public interface ConfigService {

  /**
   * Loads the config named {@code name}, or returns the existing handle if it was already loaded.
   * Missing files are created from {@code defaults}. The returned handle is cached, so repeated
   * calls with the same {@code name} and {@code type} return the same handle.
   *
   * @param name config file name (without directory)
   * @param type the type the YAML is bound to
   * @param defaults supplies the initial value when the file is absent
   * @throws IllegalStateException if {@code name} was already loaded under a different {@code type}
   */
  <T> ConfigHandle<T> load(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults);

  /**
   * Re-reads every loaded config from disk, swapping in fresh snapshots. Continues past individual
   * failures and reports them rather than aborting; see the returned {@link ReloadReport} for which
   * configs reloaded and which failed.
   */
  ReloadReport reloadAll();
}
