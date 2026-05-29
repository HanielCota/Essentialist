package com.hanielcota.essentials.modules.essentials.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import com.hanielcota.essentials.modules.essentials.menu.EssentialsModulesMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("essentials")
@Permission("essentials.admin.reload")
@Description("Comandos administrativos do Essentials.")
@Syntax("/essentials [reload]")
public record EssentialsCommand(
    ConfigHandle<EssentialsConfig> config, ConfigService configs, MenuService menus) {

  @DefaultSubcommand
  public CommandResult open(@NonNull CommandActor actor) {
    if (!actor.isPlayer()) {
      var snap = this.config.value();
      var usage = snap.usage();

      actor.sendMessage(usage);

      return CommandResult.success();
    }

    var player = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, player, EssentialsModulesMenu.ID, actor);

    return CommandResult.success();
  }

  @Subcommand("reload")
  @Description("Recarrega todas as configurações do plugin.")
  @Syntax("/essentials reload")
  public CommandResult reload(@NonNull CommandActor actor) {
    var report = this.configs.reloadAll();
    var snap = this.config.value();

    if (report.failures().isEmpty()) {
      var total = report.total();
      var successMsg = snap.formatSuccess(total);

      actor.sendSuccess(successMsg);

      return CommandResult.success();
    }

    var failedNames = report.failedNames();
    var failed = String.join(", ", failedNames);
    var succeeded = report.succeeded();
    var total = report.total();
    var failureMsg = snap.formatFailure(succeeded, total, failed);

    return CommandResult.invalidUsage(failureMsg);
  }
}
