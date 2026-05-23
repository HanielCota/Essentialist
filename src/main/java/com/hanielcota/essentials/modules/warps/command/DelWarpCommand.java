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

@Command("delwarp")
@EssentialsCommand
@Permission("essentials.warp.delete")
@Cooldown(duration = "2s")
@Description("Remove uma warp pelo nome.")
@Syntax("/delwarp <nome>")
public record DelWarpCommand(ConfigHandle<WarpsConfig> config, WarpService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @Arg("nome") String name) {
    var messages = config.value().messages();
    if (!service.delete(name)) {
      actor.sendError(messages.unknownWarp().replace("{name}", name));
      return;
    }
    actor.sendSuccess(messages.warpDeleted().replace("{name}", name));
  }
}
