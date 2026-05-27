package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptCommand;
import com.hanielcota.essentials.modules.tpa.command.TpCancelCommand;
import com.hanielcota.essentials.modules.tpa.command.TpDenyCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaBlockCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryPresenter;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaUnblockCommand;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaCommandBootstrap {

  private final @NonNull ModuleEnvironment env;
  private final @NonNull ModuleRegistrar registrar;
  private final @NonNull ConfigHandle<TpaConfig> config;

  public void registerCommands(
      @NonNull AsyncTpaHistory history,
      @NonNull TeleportRequestService requestService,
      @NonNull TpaNotifier notifier,
      @NonNull TpaBlockService blocks,
      @NonNull TpaHistoryMenuState menuState,
      @NonNull TpaTargetSelections targetSelections,
      @NonNull TpaRuntimeBootstrap.TpaShared shared) {
    var menus = this.env.service(MenuService.class);
    var playerProvider = shared.players();

    var tpaCommand = new TpaCommand(this.config, playerProvider, menus, targetSelections);
    this.registrar.command(tpaCommand);

    var tpaHereCommand = new TpaHereCommand(this.config, menus, targetSelections);
    this.registrar.command(tpaHereCommand);

    var blockCommand = new TpaBlockCommand(this.config, blocks, playerProvider, menus);
    this.registrar.command(blockCommand);

    var unblockCommand = new TpaUnblockCommand(this.config, blocks, playerProvider, menus);
    this.registrar.command(unblockCommand);

    var tpAcceptCommand =
        new TpAcceptCommand(
            this.config,
            requestService,
            shared.acceptHandler(),
            shared.incomingResolver(),
            shared.callbacks(),
            shared.actors(),
            menus);
    this.registrar.command(tpAcceptCommand);

    var tpDenyCommand =
        new TpDenyCommand(
            this.config,
            requestService,
            shared.replyNotifier(),
            shared.incomingResolver(),
            shared.actors(),
            menus);
    this.registrar.command(tpDenyCommand);

    var tpCancelCommand =
        new TpCancelCommand(this.config, requestService, notifier, shared.actors());
    this.registrar.command(tpCancelCommand);

    var historyPresenter = new TpaHistoryPresenter(this.config, history, menus, menuState);
    var tpaHistoryCommand = new TpaHistoryCommand(this.config, playerProvider, historyPresenter);
    this.registrar.command(tpaHistoryCommand);
  }
}
