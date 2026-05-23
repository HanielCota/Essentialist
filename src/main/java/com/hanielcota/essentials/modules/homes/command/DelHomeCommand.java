package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.Placeholders;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("delhome")
@EssentialsCommand
@Permission("essentials.home.delete")
@Cooldown(duration = "2s")
@Description("Remove uma home pelo nome (ou \"home\" se ausente).")
@Syntax("/delhome [nome]")
public record DelHomeCommand(ConfigHandle<HomesConfig> config, HomeService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") @Arg("nome") String rawName) {
    Player sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();
    var name = rawName.isBlank() ? snap.defaultHomeName() : rawName;

    if (!service.delete(sender.getUniqueId(), name)) {
      actor.sendError(Placeholders.format(messages.unknownHome(), "name", name));
      return;
    }
    actor.sendSuccess(Placeholders.format(messages.homeDeleted(), "name", name));
  }
}
