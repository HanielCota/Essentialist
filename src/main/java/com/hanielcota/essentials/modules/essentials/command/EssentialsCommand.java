package com.hanielcota.essentials.modules.essentials.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;

@Command("essentials")
@Permission("essentials.admin.reload")
@Description("Comandos administrativos do Essentials.")
@Syntax("/essentials reload")
public record EssentialsCommand(ConfigHandle<EssentialsConfig> config, ConfigService configs) {

  @DefaultSubcommand
  public CommandResult showUsage(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var usage = snap.usage();

    actor.sendMessage(usage);

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
