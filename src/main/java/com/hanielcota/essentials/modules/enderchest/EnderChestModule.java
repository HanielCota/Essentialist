package com.hanielcota.essentials.modules.enderchest;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.enderchest.command.EnderChestCommand;
import com.hanielcota.essentials.modules.enderchest.config.EnderChestConfig;
import com.hanielcota.essentials.modules.enderchest.listener.EnderChestQuitListener;
import lombok.NonNull;

public final class EnderChestModule extends AbstractModule {

  public EnderChestModule() {
    super("enderchest");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("enderchest", EnderChestConfig.class, EnderChestConfig::defaults);
    var command = new EnderChestCommand(config);

    registrar.command(command);
    registrar.listener(new EnderChestQuitListener());
  }
}
