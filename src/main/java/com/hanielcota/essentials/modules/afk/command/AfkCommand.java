package com.hanielcota.essentials.modules.afk.command;

import com.hanielcota.essentials.modules.afk.service.AfkService;
import com.hanielcota.essentials.modules.afk.service.AfkTransitions;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(
    value = "afk",
    aliases = {"away", "idle"})
@Permission("essentials.afk")
@Description("Marca ou remove o seu status AFK.")
@Syntax("/afk [motivo]")
public record AfkCommand(AfkService service, AfkTransitions transitions) {

  @DefaultSubcommand
  @PlayerOnly
  public CommandResult execute(
      @NonNull CommandActor sender, @GreedyString @Arg("motivo") Optional<String> motivo) {
    var player = sender.unwrap(Player.class);
    var id = player.getUniqueId();
    var name = player.getName();
    var trimmed = motivo.map(String::strip).orElse("");
    var reason = trimmed.isEmpty() ? null : trimmed;

    if (this.service.isAfk(id)) {
      var now = System.currentTimeMillis();
      this.service.recordActivity(id, now);
      this.transitions.exit(id, name);
      return CommandResult.success();
    }

    this.transitions.enter(id, name, reason);
    return CommandResult.success();
  }
}
