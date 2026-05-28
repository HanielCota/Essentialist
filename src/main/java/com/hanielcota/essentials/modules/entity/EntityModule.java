package com.hanielcota.essentials.modules.entity;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.entity.config.EntityConfig;
import com.hanielcota.essentials.modules.entity.listener.ArmorStandProtectionListener;
import com.hanielcota.essentials.modules.entity.listener.CreatureSpawnListener;
import com.hanielcota.essentials.modules.entity.listener.HangingProtectionListener;
import com.hanielcota.essentials.modules.entity.listener.ItemDespawnListener;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

public final class EntityModule extends AbstractModule {

  private static final Log LOG = Log.of(EntityModule.class);

  public EntityModule() {
    super("entity");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("entity", EntityConfig.class, EntityConfig::defaults);

    var snap = config.value();
    if (!snap.enabled()) {
      LOG.info("Entity module disabled via config (enabled: false) — no listeners registered.");
      return;
    }

    registrar.listener(new HangingProtectionListener(config));
    registrar.listener(new ArmorStandProtectionListener(config));
    registrar.listener(new ItemDespawnListener(config));
    registrar.listener(new CreatureSpawnListener(config));
  }
}
