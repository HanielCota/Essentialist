package com.hanielcota.essentials.modules.essentials.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;

@Command("essentials")
@Permission("essentials.admin.reload")
@Description("Comandos administrativos do Essentials.")
@Syntax("/essentials reload")
public record EssentialsCommand(ConfigHandle<EssentialsConfig> config, ConfigService configs) {

  @Subcommand("reload")
  @Description("Recarrega todas as configurações do plugin.")
  @Syntax("/essentials reload")
  public void reload(CommandActor actor) {
    var report = configs.reloadAll();
    var snap = config.value();
    if (report.failures().isEmpty()) {
      actor.sendSuccess(snap.formatSuccess(report.total()));
      return;
    }
    String failed = String.join(", ", report.failedNames());
    actor.sendError(snap.formatFailure(report.succeeded(), report.total(), failed));
  }
}
