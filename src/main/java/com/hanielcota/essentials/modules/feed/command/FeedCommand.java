package com.hanielcota.essentials.modules.feed.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "alimentar", aliases = "feed")
@Permission("essentials.feed")
@Description("Alimenta o jogador restaurando fome e saturação.")
@Syntax("/alimentar [jogador] | /alimentar todos")
public record FeedCommand(
    ConfigHandle<FeedConfig> config,
    FeedService service,
    PlayerProvider players,
    ActorFactory actors) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (!this.service.feed(subject)) {
      var alreadyFull = snap.whenAlreadyFull();
      var alreadyFullMsg = alreadyFull.forSender(self, name);
      return CommandResult.invalidUsage(alreadyFullMsg);
    }

    var messages = snap.whenFed();
    DualReply.send(sender, subject, this.actors, messages);
    return CommandResult.success();
  }

  @Subcommand("todos")
  @Permission("essentials.feed.all")
  @Description("Alimenta todos os jogadores online.")
  @Syntax("/alimentar todos")
  public CommandResult feedAll(@NonNull CommandActor sender) {
    var online = this.players.all();

    var fed = 0;
    for (var player : online) {
      if (this.service.feed(player)) {
        fed++;
      }
    }

    var snap = this.config.value();
    var summaryMsg = snap.formatFedAll(fed);

    sender.sendSuccess(summaryMsg);
    return CommandResult.success();
  }
}
