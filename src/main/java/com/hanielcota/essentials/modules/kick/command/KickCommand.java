package com.hanielcota.essentials.modules.kick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kick.config.KickConfig;
import com.hanielcota.essentials.modules.kick.service.KickService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("kick")
@Permission("essentials.kick")
@Cooldown(duration = "3s")
@Description("Expulsa um jogador do servidor.")
@Syntax("/kick <jogador> [motivo]")
public record KickCommand(ConfigHandle<KickConfig> config, KickService service) {

  @DefaultSubcommand
  public void execute(
      CommandActor sender,
      @OnlinePlayer Player target,
      @DefaultValue("") @GreedyString @Arg("motivo") String motivo) {
    var snap = config.value();
    var reason = snap.reasonOr(motivo.strip());

    service.kick(target, snap.formatScreen(reason));
    sender.sendSuccess(snap.formatKicked(target.getName(), reason));
  }
}
