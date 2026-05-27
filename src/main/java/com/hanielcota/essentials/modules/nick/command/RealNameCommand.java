package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.nick.service.RealNameResolver;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;

@Command("realname")
@Permission("essentials.realname")
@Cooldown(duration = "1s")
@Description("Mostra o nome real de um jogador a partir do apelido.")
@Syntax("/realname <apelido>")
public record RealNameCommand(
    ConfigHandle<NickConfig> config, NickService service, RealNameResolver resolver) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor sender, @Arg("apelido") String apelido) {
    var snap = this.config.value();
    var query = apelido.strip();
    var ownerId = this.service.idByNick(query).orElse(null);

    if (ownerId == null) {
      var unknownMsg = snap.formatUnknownNick(query);
      return CommandResult.invalidUsage(unknownMsg);
    }

    var realName = this.resolver.resolve(ownerId);
    var entry = this.service.nickOf(ownerId).orElseThrow();
    var foundMsg = snap.formatRealNameOf(entry.nickname(), realName);

    sender.sendMessage(foundMsg);

    return CommandResult.success();
  }
}
