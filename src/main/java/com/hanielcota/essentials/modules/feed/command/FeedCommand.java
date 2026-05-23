package com.hanielcota.essentials.modules.feed.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "alimentar", aliases = "feed")
@Permission("essentials.feed")
@Cooldown(duration = "5s")
@Description("Alimenta o jogador restaurando fome e saturação.")
@Syntax("/alimentar [jogador] | /alimentar todos")
public record FeedCommand(
    ConfigHandle<FeedConfig> config,
    FeedService service,
    PlayerProvider players,
    PaperCommandFramework framework) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (!this.service.feed(subject)) {
      sender.sendError(snap.whenAlreadyFull().forSender(self, name));
      return;
    }

    var messages = snap.whenFed();
    var target = this.framework.actorOf(subject);
    sender.sendDualMessage(target, messages.forSender(self, name), messages.forTarget(name));
  }

  @Subcommand("todos")
  @Permission("essentials.feed.all")
  @Description("Alimenta todos os jogadores online.")
  @Syntax("/alimentar todos")
  public void feedAll(@NonNull CommandActor sender) {
    var fed = 0;
    for (var player : this.players.all()) {
      if (this.service.feed(player)) {
        fed++;
      }
    }
    var snap = this.config.value();
    sender.sendSuccess(snap.formatFedAll(fed));
  }
}
