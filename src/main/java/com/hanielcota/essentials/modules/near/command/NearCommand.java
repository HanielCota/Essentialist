package com.hanielcota.essentials.modules.near.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("near")
@EssentialsCommand
@Permission("essentials.near")
@Cooldown(duration = "3s")
@Description("Lista os jogadores próximos.")
@Syntax("/near [raio]")
public record NearCommand(ConfigHandle<NearConfig> config, NearService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @DefaultValue("-1") @Arg("raio") int raio) {
    Player player = actor.unwrap(Player.class);
    var snap = this.config.value();

    int radius = snap.defaultRadius();
    if (raio >= 0) {
      radius = raio;
    }
    if (radius < 1 || radius > snap.maxRadius()) {
      var invalidRadiusMsg = snap.formatInvalidRadius();
      actor.sendError(invalidRadiusMsg);
      return;
    }

    var nearby = this.service.findNearby(player, radius);
    if (nearby.isEmpty()) {
      var noneMsg = snap.formatNone(radius);
      actor.sendMessage(noneMsg);
      return;
    }

    var stream = nearby.stream();
    var formatted =
        stream.map(found -> snap.formatEntry(found.player().getName(), found.distance()));
    String players = formatted.collect(Collectors.joining(snap.separator()));

    var foundMsg = snap.formatFound(radius, nearby.size(), players);
    actor.sendMessage(foundMsg);
  }
}
