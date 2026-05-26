package com.hanielcota.essentials.config;

/**
 * Snapshot accessor for a single named config file.
 *
 * <p>{@link #value()} returns the current snapshot atomically — callers should read it once per
 * operation and pass the snapshot down so a concurrent reload cannot tear a single decision across
 * pre- and post-reload state.
 */
public interface ConfigHandle<T> {

  String name();

  T value();
}
