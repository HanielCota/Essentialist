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

@Command("delwarp")
@EssentialsCommand
@Permission("essentials.warp.delete")
@Cooldown(duration = "2s")
@Description("Remove uma warp pelo nome.")
@Syntax("/delwarp <nome>")
public record DelWarpCommand(ConfigHandle<WarpsConfig> config, WarpService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @Arg("nome") String name) {
    var snap = this.config.value();
    var messages = snap.messages();

    var deleted = this.service.delete(name);
    if (!deleted) {
      var unknownTemplate = messages.unknownWarp();
      var unknownMsg = unknownTemplate.replace("{name}", name);
      actor.sendError(unknownMsg);
      return;
    }

    var deletedTemplate = messages.warpDeleted();
    var deletedMsg = deletedTemplate.replace("{name}", name);

    actor.sendSuccess(deletedMsg);
  }
}
