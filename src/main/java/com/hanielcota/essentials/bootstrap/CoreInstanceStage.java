package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.EssentialsCore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the {@link EssentialsCore} singleton and publishes it under both the {@link EssentialsApi}
 * (public surface for addons) and the concrete {@link EssentialsCore} class (so later stages such
 * as {@link EnableModulesStage} can resolve it without a separate factory).
 */
@RequiredArgsConstructor
final class CoreInstanceStage implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "core-instance";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();
    var core = new EssentialsCore(this.plugin, services);

    services.register(EssentialsApi.class, core);
    services.register(EssentialsCore.class, core);
  }
}
