package com.hanielcota.essentials.modules.feed.command;

import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command(value = "alimentar", aliases = "feed")
@EssentialsCommand
@Permission("essentials.feed")
@PermissionForOther("essentials.feed.others")
@Cooldown(duration = "5s")
@Description("Alimenta o jogador restaurando fome e saturação.")
@Syntax("/alimentar [jogador]")
public record FeedCommand(
    ConfigHandle<FeedConfig> config, FeedService service, ActorMessages messages) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    boolean fed = service.feed(subject);
    var snap = config.value();
    String name = subject.getName();

    if (!fed) {
      sender.sendError(snap.whenAlreadyFull().format(true, name));
      return;
    }

    messages.notifyTarget(sender, subject, snap.whenFed(), name);
  }
}
