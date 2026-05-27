package com.hanielcota.essentials.modules.seen.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.seen.config.SeenConfig;
import com.hanielcota.essentials.modules.seen.service.SeenService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.time.Instant;
import lombok.NonNull;

@Command("seen")
@Permission("essentials.seen")
@Cooldown(duration = "1s")
@Description("Mostra a última vez em que o jogador esteve online.")
@Syntax("/seen <jogador>")
public record SeenCommand(ConfigHandle<SeenConfig> config, SeenService service) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor sender, @Arg("jogador") String jogador) {
    var snap = this.config.value();
    var query = jogador.strip();
    var now = Instant.now();
    var line = this.service.describe(query, now).orElse(null);

    if (line == null) {
      var neverMsg = snap.formatNeverSeen(query);
      return CommandResult.invalidUsage(neverMsg);
    }

    var message = snap.formatLine(line);
    sender.sendMessage(message);

    return CommandResult.success();
  }
}
