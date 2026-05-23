package com.hanielcota.essentials.core;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.ModuleContext;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsCore implements EssentialsApi {

  private final EssentialsPlugin plugin;
  private final ServiceRegistry services;

  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  public void advance(@NonNull LifecyclePhase next) {
    this.phase = next;

    if (next == LifecyclePhase.ENABLED) {
      var moduleManager = this.services.resolve(ModuleManager.class);
      var context = newContext();

      moduleManager.enableAll(context);
    }
  }

  public void shutdown() {
    this.phase = LifecyclePhase.DISABLING;

    try {
      var moduleManager = this.services.resolve(ModuleManager.class);
      moduleManager.disableAll();
    } finally {
      var databaseProviderOpt = this.services.find(DatabaseProvider.class);
      databaseProviderOpt.ifPresent(DatabaseProvider::close);

      var menuServiceOpt = this.services.find(MenuService.class);
      menuServiceOpt.ifPresent(MenuService::shutdown);

      this.phase = LifecyclePhase.DISABLED;
    }
  }

  public LifecyclePhase phase() {
    return this.phase;
  }

  @Override
  public EssentialsPlugin plugin() {
    return this.plugin;
  }

  @Override
  public ServiceRegistry services() {
    return this.services;
  }

  private ModuleContext newContext() {
    return new ModuleContext(this.plugin, this.services);
  }
}
