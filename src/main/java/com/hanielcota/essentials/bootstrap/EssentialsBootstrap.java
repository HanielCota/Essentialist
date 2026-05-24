package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

  public EssentialsCore start() {
    var services = new DefaultServiceRegistry();

    new CoreServicesBootstrap(this.plugin).register(services);
    new DatabaseBootstrap(this.plugin).register(services);

    var modules = new ModuleDiscovery().discover();
    services.register(ModuleManager.class, modules);

    var commands = new CommandSystemBootstrap(this.plugin);
    var framework = commands.register(services, modules);
    new MenuBootstrap(this.plugin).register(services);
    new UserStackBootstrap(this.plugin).register(services);

    var core = new EssentialsCore(this.plugin, services);
    services.register(EssentialsApi.class, core);

    commands.mirrorServicesToCommands(framework, services);

    core.advance(LifecyclePhase.ENABLED);
    return core;
  }
}
