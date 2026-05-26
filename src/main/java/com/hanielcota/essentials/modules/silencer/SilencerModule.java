package com.hanielcota.essentials.modules.silencer;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.silencer.config.SilencerConfig;
import com.hanielcota.essentials.modules.silencer.listener.AdvancementMessageListener;
import com.hanielcota.essentials.modules.silencer.listener.DeathMessageListener;
import com.hanielcota.essentials.modules.silencer.listener.JoinMessageListener;
import com.hanielcota.essentials.modules.silencer.listener.QuitMessageListener;
import lombok.NonNull;

public final class SilencerModule extends AbstractModule {

  public SilencerModule() {
    super("silencer");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("silencer", SilencerConfig.class, SilencerConfig::defaults);

    var joinListener = new JoinMessageListener(config);
    var quitListener = new QuitMessageListener(config);
    var deathListener = new DeathMessageListener(config);
    var advancementListener = new AdvancementMessageListener(config);

    registrar.listener(joinListener);
    registrar.listener(quitListener);
    registrar.listener(deathListener);
    registrar.listener(advancementListener);
  }
}
