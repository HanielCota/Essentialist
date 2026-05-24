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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("kick")
@Permission("essentials.kick")
@Cooldown(duration = "3s")
@Description("Expulsa um jogador do servidor.")
@Syntax("/kick <jogador> [motivo]")
public record KickCommand(ConfigHandle<KickConfig> config, KickService service) {

  private static final String EXEMPT_PERMISSION = "essentials.kick.exempt";

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @DefaultValue("") @GreedyString @Arg("motivo") String motivo) {
    var snap = this.config.value();

    if (target.hasPermission(EXEMPT_PERMISSION)) {
      sender.sendError(snap.formatExempt(target.getName()));
      return;
    }

    var reason = snap.reasonOr(motivo.strip());

    var screenMsg = snap.formatScreen(reason);
    this.service.kick(target, screenMsg);

    var kickedMsg = snap.formatKicked(target.getName(), reason);
    sender.sendSuccess(kickedMsg);
  }
}
