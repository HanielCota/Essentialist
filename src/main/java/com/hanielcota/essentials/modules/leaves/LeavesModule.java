package com.hanielcota.essentials.modules.leaves;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.leaves.config.LeavesConfig;
import com.hanielcota.essentials.modules.leaves.listener.LeafDecayListener;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

public final class LeavesModule extends AbstractModule {

  private static final Log LOG = Log.of(LeavesModule.class);

  public LeavesModule() {
    super("leaves");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("leaves", LeavesConfig.class, LeavesConfig::defaults);

    var snap = config.value();
    if (!snap.enabled()) {
      LOG.info("Leaves module disabled via config (enabled: false) — no listener registered.");
      return;
    }

    var listener = new LeafDecayListener(config);

    registrar.listener(listener);
  }
}
