package com.hanielcota.essentials.core;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.registration.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.Optional;
import lombok.NonNull;

/**
 * Thin public API facade for the Essentialist plugin. Delegates lifecycle management to {@link
 * CoreLifecycle} and service resolution to {@link ServiceRegistry}.
 */
public final class EssentialsCore implements EssentialsApi {

  private final EssentialsPlugin plugin;
  private final ServiceRegistry services;
  private final CoreLifecycle lifecycle;

  public EssentialsCore(@NonNull EssentialsPlugin plugin, @NonNull ServiceRegistry services) {
    this.plugin = plugin;
    this.services = services;
    this.lifecycle = new CoreLifecycle(services);
  }

  public void advance(@NonNull LifecyclePhase next) {
    this.lifecycle.advance(next);

    if (next == LifecyclePhase.ENABLED) {
      var moduleManager = this.services.resolve(ModuleManager.class);
      var context = new ModuleContext(this.plugin, this.services);

      moduleManager.enableAll(context);
    }
  }

  public void shutdown() {
    this.lifecycle.shutdown();
  }

  public LifecyclePhase phase() {
    return this.lifecycle.phase();
  }

  @Override
  public <T> Optional<T> api(@NonNull Class<T> apiType) {
    return this.services.findAssignable(apiType);
  }
}
