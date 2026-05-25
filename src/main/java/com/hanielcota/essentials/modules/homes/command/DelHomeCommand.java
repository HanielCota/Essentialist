package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("delhome")
@EssentialsCommand
@Permission("essentials.home.delete")
@Cooldown(duration = "2s")
@Description("Remove uma home pelo nome (ou \"home\" se ausente).")
@Syntax("/delhome [nome]")
public record DelHomeCommand(
    ConfigHandle<HomesConfig> config, HomeService service, HomeNameResolver nameResolver) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @DefaultValue("") @Arg("nome") String rawName) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();
    var name = this.nameResolver.resolve(rawName);

    if (name == null) {
      var invalidNameMsg = messages.invalidName();
      actor.sendError(invalidNameMsg);
      return;
    }

    var uuid = sender.getUniqueId();

    if (!this.service.delete(uuid, name)) {
      var unknownHomeMsg = messages.unknownHome();
      var unknownMsg = unknownHomeMsg.replace("{name}", name);
      actor.sendError(unknownMsg);
      return;
    }

    var homeDeletedMsg = messages.homeDeleted();
    var deletedMsg = homeDeletedMsg.replace("{name}", name);

    actor.sendSuccess(deletedMsg);
  }
}
