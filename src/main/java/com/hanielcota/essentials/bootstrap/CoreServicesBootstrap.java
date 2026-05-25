package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.config.YamlConfigService;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.BukkitPlayerProvider;
import com.hanielcota.essentials.paper.PaperAudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.PaperScheduler;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.service.ServiceRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class CoreServicesBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "core-services";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();
    registerScheduler(services);
    registerPaperAdapters(services);
    registerConfigs(services);
  }

  private void registerScheduler(@NonNull ServiceRegistry services) {
    var scheduler = new PaperScheduler(this.plugin);
    services.register(Scheduler.class, scheduler);

    var mainThreadCallbacks = new MainThreadCallbacks(scheduler);
    services.register(MainThreadCallbacks.class, mainThreadCallbacks);
  }

  private void registerPaperAdapters(@NonNull ServiceRegistry services) {
    var audienceProvider = new PaperAudienceProvider(this.plugin);
    services.register(AudienceProvider.class, audienceProvider);

    var playerProvider = new BukkitPlayerProvider(this.plugin);
    services.register(PlayerProvider.class, playerProvider);
  }

  private void registerConfigs(@NonNull ServiceRegistry services) {
    var dataFolder = this.plugin.getDataFolder();
    var dataFolderPath = dataFolder.toPath();
    var configDir = dataFolderPath.resolve("modules");

    var configService = new YamlConfigService(configDir);
    services.register(ConfigService.class, configService);
  }
}
