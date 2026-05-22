package com.hanielcota.essentials.core;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.ModuleContext;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.Objects;

public final class EssentialsCore implements EssentialsApi {

  private final EssentialsPlugin plugin;
  private final ServiceRegistry services;

  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  public EssentialsCore(EssentialsPlugin plugin, ServiceRegistry services) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.services = Objects.requireNonNull(services, "services");
  }

  public void advance(LifecyclePhase next) {
    this.phase = next;
    if (next == LifecyclePhase.ENABLED) {
      services.resolve(ModuleManager.class).enableAll(newContext());
    }
  }

  public void shutdown() {
    phase = LifecyclePhase.DISABLING;
    try {
      services.resolve(ModuleManager.class).disableAll();
    } finally {
      services.find(DatabaseProvider.class).ifPresent(DatabaseProvider::close);
      services.find(MenuService.class).ifPresent(MenuService::shutdown);
      phase = LifecyclePhase.DISABLED;
    }
  }

  public LifecyclePhase phase() {
    return phase;
  }

  @Override
  public EssentialsPlugin plugin() {
    return plugin;
  }

  @Override
  public ServiceRegistry services() {
    return services;
  }

  private ModuleContext newContext() {
    return new ModuleContext(plugin, services);
  }
}
