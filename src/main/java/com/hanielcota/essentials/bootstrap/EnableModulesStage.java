package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import lombok.NonNull;

/**
 * Drives the {@link EssentialsCore} into {@link LifecyclePhase#ENABLED}, which kicks off
 * dependency-ordered enable of every registered module.
 */
final class EnableModulesStage implements BootstrapStage {

  @Override
  public String name() {
    return "enable-modules";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var core = context.services().resolve(EssentialsCore.class);
    core.advance(LifecyclePhase.ENABLED);
  }
}
