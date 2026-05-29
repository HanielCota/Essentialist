package com.hanielcota.essentials.modules.combat;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.combat.config.CombatConfig;
import com.hanielcota.essentials.modules.combat.listener.DamageImmunityListener;
import com.hanielcota.essentials.modules.combat.listener.HungerListener;
import com.hanielcota.essentials.modules.combat.listener.KeepOnDeathListener;
import com.hanielcota.essentials.modules.combat.listener.PvpListener;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

public final class CombatModule extends AbstractModule {

  private static final Log LOG = Log.of(CombatModule.class);

  public CombatModule() {
    super("combat");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("combat", CombatConfig.class, CombatConfig::defaults);

    var snap = config.value();
    if (!snap.enabled()) {
      LOG.info("Combat module disabled via config (enabled: false) — no listeners registered.");
      return;
    }

    registrar.listener(new PvpListener(config));
    registrar.listener(new DamageImmunityListener(config));
    registrar.listener(new HungerListener(config));
    registrar.listener(new KeepOnDeathListener(config));
  }
}
