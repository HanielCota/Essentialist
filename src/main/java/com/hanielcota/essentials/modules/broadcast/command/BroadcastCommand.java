package com.hanielcota.essentials.modules.broadcast.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.broadcast.config.BroadcastConfig;
import com.hanielcota.essentials.modules.broadcast.service.BroadcastService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;

@Command(
    value = "broadcast",
    aliases = {"bc", "anuncio"})
@Permission("essentials.broadcast")
@Description("Envia um anúncio para todo o servidor.")
@Syntax("/broadcast <mensagem>")
public record BroadcastCommand(ConfigHandle<BroadcastConfig> config, BroadcastService service) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    var snap = this.config.value();
    var body = mensagem.strip();

    if (body.isEmpty()) {
      var usageMsg = snap.usage();

      return CommandResult.invalidUsage(usageMsg);
    }

    this.service.broadcast(body);

    return CommandResult.success();
  }
}
