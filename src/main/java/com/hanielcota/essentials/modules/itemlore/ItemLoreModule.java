package com.hanielcota.essentials.modules.itemlore;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.itemlore.command.ItemLoreCommand;
import com.hanielcota.essentials.modules.itemlore.config.ItemLoreConfig;
import com.hanielcota.essentials.modules.itemlore.service.ItemLoreService;
import lombok.NonNull;

public final class ItemLoreModule extends AbstractModule {

  public ItemLoreModule() {
    super("itemlore");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("itemlore", ItemLoreConfig.class, ItemLoreConfig::defaults);
    var service = new ItemLoreService();

    var itemLoreCommand = new ItemLoreCommand(config, service);
    registrar.command(itemLoreCommand);
  }
}
