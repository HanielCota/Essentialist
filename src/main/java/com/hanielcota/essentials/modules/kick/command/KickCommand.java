package com.hanielcota.essentials.modules.kick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kick.config.KickConfig;
import com.hanielcota.essentials.util.ComponentUtils;
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
public record KickCommand(ConfigHandle<KickConfig> config) {

  private static final String EXEMPT_PERMISSION = "essentials.kick.exempt";

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @DefaultValue("") @GreedyString @Arg("motivo") String motivo) {
    var snap = this.config.value();
    var name = target.getName();

    if (target.hasPermission(EXEMPT_PERMISSION)) {
      var exemptMsg = snap.formatExempt(name);
      sender.sendError(exemptMsg);
      return;
    }

    var trimmed = motivo.strip();
    var reason = snap.reasonOr(trimmed);
    var screenMsg = snap.formatScreen(reason);
    var screenComponent = ComponentUtils.mini(screenMsg);

    target.kick(screenComponent);

    var kickedMsg = snap.formatKicked(name, reason);
    sender.sendSuccess(kickedMsg);
  }
}
