package com.hanielcota.essentials.modules.environment;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import com.hanielcota.essentials.modules.environment.listener.BucketRestrictionListener;
import com.hanielcota.essentials.modules.environment.listener.ExplosionProtectionListener;
import com.hanielcota.essentials.modules.environment.listener.FireProtectionListener;
import com.hanielcota.essentials.modules.environment.listener.FluidFlowListener;
import com.hanielcota.essentials.modules.environment.listener.IceSnowListener;
import com.hanielcota.essentials.modules.environment.listener.LightningTransformListener;
import com.hanielcota.essentials.modules.environment.listener.MobGriefListener;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

public final class EnvironmentModule extends AbstractModule {

  private static final Log LOG = Log.of(EnvironmentModule.class);

  public EnvironmentModule() {
    super("environment");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("environment", EnvironmentConfig.class, EnvironmentConfig::defaults);

    var snap = config.value();
    if (!snap.enabled()) {
      LOG.info(
          "Environment module disabled via config (enabled: false) — no listeners registered.");
      return;
    }

    registrar.listener(new FireProtectionListener(config));
    registrar.listener(new ExplosionProtectionListener(config));
    registrar.listener(new FluidFlowListener(config));
    registrar.listener(new BucketRestrictionListener(config));
    registrar.listener(new IceSnowListener(config));
    registrar.listener(new MobGriefListener(config));
    registrar.listener(new LightningTransformListener(config));
  }
}
