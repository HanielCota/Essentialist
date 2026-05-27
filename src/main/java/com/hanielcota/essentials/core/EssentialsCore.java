package com.hanielcota.essentials.core;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.api.HomesApi;
import com.hanielcota.essentials.api.MutesApi;
import com.hanielcota.essentials.api.NicksApi;
import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.api.VanishApi;
import com.hanielcota.essentials.api.WarpsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.connection.DatabaseProvider;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.registration.ModuleManager;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.shared.Log;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsCore implements EssentialsApi {

  private static final Log LOG = Log.of(EssentialsCore.class);

  private final EssentialsPlugin plugin;
  private final ServiceRegistry services;

  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  private static void safelyShutdown(@NonNull String label, @NonNull Runnable step) {
    try {
      step.run();
    } catch (RuntimeException e) {
      LOG.error(e, "{} shutdown failed", label);
    }
  }

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
      // Shut down MenuService before the database: menu teardown closes open viewers via
      // InventoryCloseEvent listeners, some of which (e.g. invsee release, homes session cleanup)
      // may still touch services that hit SQL. Both steps are wrapped so a thrown MenuService
      // shutdown never strands the HikariCP pool / SQLite file open.
      safelyShutdown("MenuService", this::shutdownMenuService);
      safelyShutdown("DatabaseProvider", this::shutdownDatabase);

      this.phase = LifecyclePhase.DISABLED;
    }
  }

  private void shutdownMenuService() {
    var menuHandle = this.services.find(MenuService.class);
    menuHandle.ifPresent(MenuService::shutdown);
  }

  private void shutdownDatabase() {
    var databaseHandle = this.services.find(DatabaseProvider.class);
    databaseHandle.ifPresent(DatabaseProvider::close);
  }

  public LifecyclePhase phase() {
    return this.phase;
  }

  @Override
  public Optional<HomesApi> homes() {
    return this.services.find(HomeService.class).map(HomesApi.class::cast);
  }

  @Override
  public Optional<WarpsApi> warps() {
    return this.services.find(WarpService.class).map(WarpsApi.class::cast);
  }

  @Override
  public Optional<MutesApi> mutes() {
    return this.services.find(MuteService.class).map(MutesApi.class::cast);
  }

  @Override
  public Optional<NicksApi> nicks() {
    return this.services.find(NickService.class).map(NicksApi.class::cast);
  }

  @Override
  public Optional<VanishApi> vanish() {
    return this.services.find(VanishService.class).map(VanishApi.class::cast);
  }

  @Override
  public Optional<TeleportsApi> teleports() {
    return this.services.find(TeleportService.class).map(TeleportsApi.class::cast);
  }

  private ModuleContext newContext() {
    return new ModuleContext(this.plugin, this.services);
  }
}
