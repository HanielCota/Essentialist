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
      var alreadyFull = snap.whenAlreadyFull();
      var alreadyFullMsg = alreadyFull.forSender(self, name);
      sender.sendError(alreadyFullMsg);
      return;
    }

    var messages = snap.whenFed();
    var target = this.framework.actorOf(subject);

    var senderMsg = messages.forSender(self, name);
    var targetMsg = messages.forTarget(name);

    sender.sendDualMessage(target, senderMsg, targetMsg);
  }

  @Subcommand("todos")
  @Permission("essentials.feed.all")
  @Description("Alimenta todos os jogadores online.")
  @Syntax("/alimentar todos")
  public void feedAll(@NonNull CommandActor sender) {
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
  }
}
