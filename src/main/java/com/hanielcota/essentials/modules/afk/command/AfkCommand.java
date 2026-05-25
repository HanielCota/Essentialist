package com.hanielcota.essentials.modules.afk.command;

import com.hanielcota.essentials.modules.afk.service.AfkBroadcaster;
import com.hanielcota.essentials.modules.afk.service.AfkService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(
    value = "afk",
    aliases = {"away", "idle"})
@Permission("essentials.afk")
@Cooldown(duration = "1s")
@Description("Marca ou remove o seu status AFK.")
@Syntax("/afk [motivo]")
public record AfkCommand(AfkService service, AfkBroadcaster broadcaster) {

  @DefaultSubcommand
  @PlayerOnly
  public void execute(
      @NonNull CommandActor sender, @DefaultValue("") @GreedyString @Arg("motivo") String motivo) {
    var player = sender.unwrap(Player.class);
    var id = player.getUniqueId();
    var name = player.getName();
    var trimmed = motivo.strip();
    var reason = trimmed.isEmpty() ? null : trimmed;

    if (this.service.isAfk(id)) {
      var now = System.currentTimeMillis();
      this.service.recordActivity(id, now);
      this.service.exit(id);
      this.broadcaster.broadcastExit(name);
      return;
    }

    this.service.enter(id, reason);
    this.broadcaster.broadcastEnter(name, reason);
  }
}
