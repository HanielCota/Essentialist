package com.hanielcota.essentials.core;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.api.HomesApi;
import com.hanielcota.essentials.api.MutesApi;
import com.hanielcota.essentials.api.NicksApi;
import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.api.VanishApi;
import com.hanielcota.essentials.api.WarpsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.registration.ModuleManager;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.warps.service.WarpService;
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
}
