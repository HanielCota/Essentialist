package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("setwarp")
@EssentialsCommand
@Permission("essentials.warp.set")
@Cooldown(duration = "2s")
@Description("Cria ou sobrescreve uma warp na sua localização atual.")
@Syntax("/setwarp <nome>")
public record SetWarpCommand(ConfigHandle<WarpsConfig> config, WarpService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @Arg("nome") String name) {
    var sender = actor.unwrap(Player.class);
    var messages = this.config.value().messages();
    var existed = this.service.find(name).isPresent();

    this.service.save(name, sender);
    var template = existed ? messages.warpUpdated() : messages.warpSet();
    actor.sendSuccess(template.replace("{name}", name));
  }
}
