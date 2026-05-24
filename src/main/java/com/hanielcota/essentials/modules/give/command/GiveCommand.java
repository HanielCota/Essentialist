package com.hanielcota.essentials.modules.give.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveResult;
import com.hanielcota.essentials.modules.give.service.GiveService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Min;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
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
@Syntax("/give <item> [quantidade] | /give para <jogador> <item> [quantidade]")
public record GiveCommand(
    ConfigHandle<GiveConfig> config,
    GiveService service,
    PlayerProvider players,
    PaperCommandFramework framework) {

  private static String fill(
      @NonNull String template, @NonNull String item, int amount, int leftover) {
    var withItem = template.replace("{item}", item);
    var withAmount = withItem.replace("{amount}", Integer.toString(amount));
    return withAmount.replace("{leftover}", Integer.toString(leftover));
  }

  private static String format(
      @NonNull String template, @NonNull String item, @NonNull GiveResult result) {
    return fill(template, item, result.given(), result.leftover());
  }

  /**
   * Gives the caller (self) the item. CommandFramework parameter parsing is positional-strict and
   * does not backtrack: trying to fit both an optional amount and an optional target into the same
   * default subcommand made {@code /give DIAMOND Alice} unparsable (Alice fed to the int parser).
   * Self-only here, with an explicit {@code /give para} subcommand for the other-player case.
   */
  @DefaultSubcommand
  @PlayerOnly
  public void execute(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var subject = sender.unwrap(Player.class);
    deliver(sender, subject, item, amount, true);
  }

  @Subcommand({"para", "to"})
  @Permission("essentials.give.others")
  @Description("Dá itens a outro jogador.")
  @Syntax("/give para <jogador> <item> [quantidade]")
  public void executeFor(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player subject,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var self = Senders.isSelf(sender, subject);
    deliver(sender, subject, item, amount, self);
  }

  private void deliver(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull Material item,
      int amount,
      boolean self) {
    var snap = this.config.value();
    var name = subject.getName();

    if (!item.isItem()) {
      sender.sendError(snap.invalidItem());
      return;
    }
    if (amount > snap.maxAmount()) {
      sender.sendError(snap.formatAmountTooLarge());
      return;
    }

    var result = this.service.giveResult(subject, item, amount);
    var itemName = item.name().toLowerCase(Locale.ROOT);

    if (result.noneGiven()) {
      sender.sendError(
          fill(
              snap.whenInventoryFull().forSender(self, name),
              itemName,
              result.given(),
              result.leftover()));
      return;
    }

    var messages = snap.whenGiven();
    if (result.partial()) {
      messages = snap.whenPartial();
    }

    if (self) {
      sender.sendSuccess(format(messages.forSender(true, name), itemName, result));
      return;
    }

    var target = this.framework.actorOf(subject);
    var selfMessage = format(messages.forSender(false, name), itemName, result);
    var targetMessage = format(messages.forTarget(name), itemName, result);
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
      var result = this.service.giveResult(player, item, amount);
      var recipient = this.framework.actorOf(player);

      if (result.noneGiven()) {
        recipient.sendError(
            format(snap.whenInventoryFull().forTarget(player.getName()), itemName, result));
        continue;
      }

      count++;
      var messages = snap.whenGiven();
      if (result.partial()) {
        messages = snap.whenPartial();
      }
      recipient.sendSuccess(format(messages.forTarget(player.getName()), itemName, result));
    }

    var givenAllMsg = snap.formatGivenAll(itemName, amount, count);
    sender.sendSuccess(givenAllMsg);
  }
}
