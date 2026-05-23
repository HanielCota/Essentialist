package com.hanielcota.essentials.modules.give.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Min;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Locale;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command("give")
@Permission("essentials.give")
@Cooldown(duration = "3s")
@Description("Dá itens a um jogador.")
@Syntax("/give <item> [quantidade] [jogador]")
public record GiveCommand(
    ConfigHandle<GiveConfig> config,
    GiveService service,
    PlayerProvider players,
    PaperCommandFramework framework) {

  private static String fill(
      @NonNull String template, @NonNull String item, int amount, int leftover) {
    return template
        .replace("{item}", item)
        .replace("{amount}", Integer.toString(amount))
        .replace("{leftover}", Integer.toString(leftover));
  }

  @DefaultSubcommand
  @PermissionForOther(".others")
  public void execute(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount,
      @TargetOrSelf Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (!item.isItem()) {
      sender.sendError(snap.invalidItem());
      return;
    }

    if (amount > snap.maxAmount()) {
      sender.sendError(snap.formatAmountTooLarge());
      return;
    }

    var leftover = this.service.give(subject, item, amount);
    var given = amount - leftover;
    var itemName = item.name().toLowerCase(Locale.ROOT);

    if (given == 0) {
      sender.sendError(
          fill(snap.whenInventoryFull().forSender(self, name), itemName, given, leftover));
      return;
    }

    var messages = leftover > 0 ? snap.whenPartial() : snap.whenGiven();
    var target = this.framework.actorOf(subject);
    var selfMessage = fill(messages.forSender(self, name), itemName, given, leftover);
    var targetMessage = fill(messages.forTarget(name), itemName, given, leftover);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }

  @Subcommand({"all", "todos"})
  @Permission("essentials.give.all")
  @Description("Dá um item para todos os jogadores online.")
  @Syntax("/give all <item> [quantidade]")
  public void all(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var snap = this.config.value();

    if (!item.isItem()) {
      sender.sendError(snap.invalidItem());
      return;
    }

    if (amount > snap.maxAmount()) {
      sender.sendError(snap.formatAmountTooLarge());
      return;
    }

    var itemName = item.name().toLowerCase(Locale.ROOT);
    var count = 0;

    for (var player : this.players.all()) {
      var leftover = this.service.give(player, item, amount);
      var given = amount - leftover;
      var recipient = this.framework.actorOf(player);

      if (given == 0) {
        recipient.sendError(
            fill(snap.whenInventoryFull().forTarget(player.getName()), itemName, given, leftover));
        continue;
      }

      count++;
      var messages = leftover > 0 ? snap.whenPartial() : snap.whenGiven();
      recipient.sendSuccess(fill(messages.forTarget(player.getName()), itemName, given, leftover));
    }

    var givenAllMsg = snap.formatGivenAll(itemName, amount, count);
    sender.sendSuccess(givenAllMsg);
  }
}
