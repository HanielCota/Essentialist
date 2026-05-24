package com.hanielcota.essentials.modules.homes.factory;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.command.DelHomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomesCommand;
import com.hanielcota.essentials.modules.homes.command.SetHomeCommand;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.material.HomeMaterialResolver;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import java.util.List;
import lombok.NonNull;

public final class HomesCommandFactory {

  public List<Object> create(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull MenuService menus,
      @NonNull HomeTeleporter teleporter,
      @NonNull HomesMenuState menuState,
      @NonNull HomeNameResolver nameResolver) {

    var materialResolver = new HomeMaterialResolver(config);

    return List.of(
        new SetHomeCommand(config, homeService, nameResolver, materialResolver),
        new HomeCommand(config, homeService, teleporter, nameResolver),
        new DelHomeCommand(config, homeService, nameResolver),
        new HomesCommand(config, homeService, menus, menuState));
  }
}
