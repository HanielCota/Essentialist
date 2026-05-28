package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.accept.TpAcceptCommand;
import com.hanielcota.essentials.modules.tpa.command.TpCancelCommand;
import com.hanielcota.essentials.modules.tpa.command.TpDenyCommand;
import com.hanielcota.essentials.modules.tpa.command.block.TpaBlockCommand;
import com.hanielcota.essentials.modules.tpa.command.send.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.send.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.history.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.command.history.TpaHistoryPresenter;
import com.hanielcota.essentials.modules.tpa.command.block.TpaUnblockCommand;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.menu.history.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaTargetSelections;
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
            menus,
            requestService,
            shared.acceptHandler(),
            shared.teleportNotifier(),
            shared.incomingResolver(),
            shared.actors(),
            shared.callbacks());
    this.registrar.command(tpAcceptCommand);

    var tpDenyCommand =
        new TpDenyCommand(
            this.config,
            menus,
            requestService,
            shared.replyNotifier(),
            shared.incomingResolver(),
            shared.actors());
    this.registrar.command(tpDenyCommand);

    var tpCancelCommand = new TpCancelCommand(menus);
    this.registrar.command(tpCancelCommand);

    var historyPresenter = new TpaHistoryPresenter(this.config, history, menus, menuState);
    var tpaHistoryCommand = new TpaHistoryCommand(this.config, playerProvider, historyPresenter);
    this.registrar.command(tpaHistoryCommand);
  }
}
