package com.hanielcota.essentials.modules.crops;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.crops.command.CropsNotifier;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import com.hanielcota.essentials.modules.crops.listener.AutoReplantListener;
import com.hanielcota.essentials.modules.crops.listener.CropBreakListener;
import com.hanielcota.essentials.modules.crops.listener.CropsNotifierCleanupListener;
import com.hanielcota.essentials.modules.crops.listener.ExplosionCropListener;
import com.hanielcota.essentials.modules.crops.listener.FarmlandHydrationListener;
import com.hanielcota.essentials.modules.crops.listener.FarmlandTrampleListener;
import com.hanielcota.essentials.modules.crops.listener.MobCropProtectionListener;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

public final class CropsModule extends AbstractModule {

  private static final Log LOG = Log.of(CropsModule.class);

  public CropsModule() {
    super("crops");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("crops", CropsConfig.class, CropsConfig::defaults);

    var snap = config.value();
    if (!snap.enabled()) {
      LOG.info("Crops module disabled via config (enabled: false) — no listeners registered.");
      return;
    }

    var notifier = new CropsNotifier(config);

    registrar.listener(new CropBreakListener(config, notifier));
    registrar.listener(new FarmlandTrampleListener(config, notifier));
    registrar.listener(new CropsNotifierCleanupListener(notifier));
    registrar.listener(new AutoReplantListener(config));
    registrar.listener(new MobCropProtectionListener(config));
    registrar.listener(new ExplosionCropListener(config));
    registrar.listener(new FarmlandHydrationListener(config));
  }
}
