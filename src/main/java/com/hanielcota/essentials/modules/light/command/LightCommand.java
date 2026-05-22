package com.hanielcota.essentials.modules.light.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.light.config.LightConfig;
import com.hanielcota.essentials.modules.light.service.LightService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command(value = "luz", aliases = "light")
@EssentialsCommand
@Permission("essentials.light")
@PermissionForOther("essentials.light.others")
@Cooldown(duration = "5s")
@Description("Ativa ou desativa a visão noturna do jogador.")
@Syntax("/luz [jogador]")
public record LightCommand(
    ConfigHandle<LightConfig> config, LightService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    boolean enabled = service.toggle(subject);
    var pair = config.value().toggle(enabled);
    String name = subject.getName();
    boolean self = sender.uniqueId().equals(subject.getUniqueId().toString());
    var target = framework.actorOf(subject);
    String selfMessage = pair.forSender(self, name);

    sender.sendDualMessage(target, selfMessage, pair.forTarget(name));
  }
}
