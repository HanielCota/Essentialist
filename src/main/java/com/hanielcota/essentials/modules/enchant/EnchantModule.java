package com.hanielcota.essentials.modules.enchant;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.enchant.command.EnchantCommand;
import com.hanielcota.essentials.modules.enchant.config.EnchantConfig;
import com.hanielcota.essentials.modules.enchant.service.EnchantService;
import lombok.NonNull;

public final class EnchantModule extends AbstractModule {

  public EnchantModule() {
    super("enchant");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("enchant", EnchantConfig.class, EnchantConfig::defaults);
    var enchantService = new EnchantService(config);
    var enchantCommand = new EnchantCommand(config, enchantService);
    registrar.command(enchantCommand);
  }
}
