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
import lombok.NonNull;

@Command("essentials")
@Permission("essentials.admin.reload")
@Description("Comandos administrativos do Essentials.")
@Syntax("/essentials reload")
public record EssentialsCommand(ConfigHandle<EssentialsConfig> config, ConfigService configs) {

  @DefaultSubcommand
  public void showUsage(@NonNull CommandActor actor) {
    actor.sendMessage(this.config.value().usage());
  }

  @Subcommand("reload")
  @Description("Recarrega todas as configurações do plugin.")
  @Syntax("/essentials reload")
  public void reload(@NonNull CommandActor actor) {
    var report = this.configs.reloadAll();
    var snap = this.config.value();

    if (report.failures().isEmpty()) {
      var successMsg = snap.formatSuccess(report.total());
      actor.sendSuccess(successMsg);
      return;
    }

    var failed = String.join(", ", report.failedNames());
    var failureMsg = snap.formatFailure(report.succeeded(), report.total(), failed);
    actor.sendError(failureMsg);
  }
}
