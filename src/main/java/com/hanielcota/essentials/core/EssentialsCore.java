package com.hanielcota.essentials.core;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Thin public API facade for the Essentialist plugin. Delegates lifecycle management to {@link
 * CoreLifecycle} and service resolution to {@link ServiceRegistry}.
 */
@RequiredArgsConstructor
public final class EssentialsCore implements EssentialsApi {

  private final @NonNull ServiceRegistry services;
  private final @NonNull CoreLifecycle lifecycle;

  /** Builds a core wired to its own {@link CoreLifecycle} over the given plugin and registry. */
  public static EssentialsCore createDefault(
      @NonNull EssentialsPlugin plugin, @NonNull ServiceRegistry services) {
    var lifecycle = new CoreLifecycle(plugin, services);
    return new EssentialsCore(services, lifecycle);
  }

  /**
   * Moves the plugin into {@code next}. Advancing to {@link LifecyclePhase#ENABLED} enables every
   * registered module as a side effect; other transitions only record the phase.
   */
  public void advance(@NonNull LifecyclePhase next) {
    this.lifecycle.advance(next);
  }

  /** Disables all modules, then runs registered shutdown steps in reverse-registration order. */
  public void shutdown() {
    this.lifecycle.shutdown();
  }

  /** The current lifecycle phase. */
  public LifecyclePhase phase() {
    return this.lifecycle.phase();
  }

  @Override
  public <T> Optional<T> api(@NonNull Class<T> apiType) {
    return this.services.find(apiType);
  }
}
