package com.hanielcota.essentials.modules.homes.factory;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.listener.HomeRenameChatListener;
import com.hanielcota.essentials.modules.homes.listener.HomesSessionCleanupListener;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.name.DefaultHomeNameValidator;
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class HomesInteractionFactory {

  public HomesInteractions create(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull Scheduler scheduler,
      @NonNull DelayedTeleport delayed,
      @NonNull PaperCommandFramework framework) {

    var actionTarget = new HomesActionTarget();
    var renameSessions = new HomeRenameSessions();
    var nameValidator = new DefaultHomeNameValidator();

    var nameResolver = new HomeNameResolver(config, nameValidator);
    var teleporter = new HomeTeleporter(config, delayed, framework);
    var teleportListener = new HomesSessionCleanupListener(actionTarget, renameSessions);

    var rename =
        new HomeRenameOrchestrator(config, homeService, scheduler, renameSessions, nameValidator);
    var renameListener = new HomeRenameChatListener(rename, renameSessions);

    return new HomesInteractions(
        teleporter, actionTarget, nameResolver, teleportListener, rename, renameListener);
  }

  public record HomesInteractions(
      HomeTeleporter teleporter,
      HomesActionTarget actionTarget,
      HomeNameResolver nameResolver,
      HomesSessionCleanupListener teleportListener,
      HomeRenameOrchestrator rename,
      HomeRenameChatListener renameListener) {}
}
