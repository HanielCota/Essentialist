package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import lombok.NonNull;

@Command("realname")
@Permission("essentials.realname")
@Cooldown(duration = "1s")
@Description("Mostra o nome real de um jogador a partir do apelido.")
@Syntax("/realname <apelido>")
public record RealNameCommand(
    ConfigHandle<NickConfig> config, NickService service, PlayerProvider players) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @Arg("apelido") String apelido) {
    var snap = this.config.value();
    var query = apelido.strip();
    var ownerId = this.service.idByNick(query).orElse(null);

    if (ownerId == null) {
      var unknownMsg = snap.formatUnknownNick(query);
      sender.sendError(unknownMsg);
      return;
    }

    var realName = resolveRealName(ownerId);
    var entry = this.service.nickFor(ownerId).orElseThrow();
    var foundMsg = snap.formatRealNameOf(entry.nickname(), realName);

    sender.sendMessage(foundMsg);
  }

  private String resolveRealName(@NonNull UUID id) {
    var online = this.players.online(id).orElse(null);
    if (online != null) {
      return online.getName();
    }

    var entry = this.service.nickFor(id).orElse(null);
    if (entry != null) {
      return entry.realName();
    }

    var offline = this.players.offline(id);
    var stored = offline.getName();

    return stored != null ? stored : id.toString();
  }
}
