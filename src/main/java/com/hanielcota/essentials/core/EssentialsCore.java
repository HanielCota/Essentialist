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

  public static EssentialsCore createDefault(
      @NonNull EssentialsPlugin plugin, @NonNull ServiceRegistry services) {
    var lifecycle = new CoreLifecycle(plugin, services);
    return new EssentialsCore(services, lifecycle);
  }

  public void advance(@NonNull LifecyclePhase next) {
    this.lifecycle.advance(next);
  }

  public void shutdown() {
    this.lifecycle.shutdown();
  }

  public LifecyclePhase phase() {
    return this.lifecycle.phase();
  }

  @Override
  public <T> Optional<T> api(@NonNull Class<T> apiType) {
    return this.services.find(apiType);
  }
}
