package com.hanielcota.essentials.api;

import java.util.Optional;

/**
 * Public entry point for addons that depend on Essentialist.
 *
 * <p>Module facades are resolved by their API interface type — add a new module and its facade is
 * reachable through {@link #api(Class)} with zero changes to this contract.
 *
 * <p>Resolution is {@link Optional} so addons gracefully degrade when a module is disabled.
 */
public interface EssentialsApi {

  /**
   * Returns the module facade registered for {@code apiType}, or empty when the module is disabled
   * or not installed.
   */
  <T> Optional<T> api(Class<T> apiType);
}
