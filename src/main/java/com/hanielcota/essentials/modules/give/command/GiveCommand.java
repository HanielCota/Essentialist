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
    var amountStr = Integer.toString(amount);
    var leftoverStr = Integer.toString(leftover);

    var withItem = template.replace("{item}", item);
    var withAmount = withItem.replace("{amount}", amountStr);

    return withAmount.replace("{leftover}", leftoverStr);
  }

  private static String format(
      @NonNull String template, @NonNull String item, @NonNull GiveResult result) {
    var given = result.given();
    var leftover = result.leftover();

    return fill(template, item, given, leftover);
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
      var invalidMsg = snap.invalidItem();
      sender.sendError(invalidMsg);
      return;
    }

    if (amount > snap.maxAmount()) {
      var tooLargeMsg = snap.formatAmountTooLarge();
      sender.sendError(tooLargeMsg);
      return;
    }

    var result = this.service.giveResult(subject, item, amount);
    var rawName = item.name();
    var itemName = rawName.toLowerCase(Locale.ROOT);

    if (result.noneGiven()) {
      var fullPair = snap.whenInventoryFull();
      var fullTemplate = fullPair.forSender(self, name);
      var given = result.given();
      var leftover = result.leftover();
      var fullMsg = fill(fullTemplate, itemName, given, leftover);

      sender.sendError(fullMsg);
      return;
    }

    var messages = snap.whenGiven();
    if (result.partial()) {
      messages = snap.whenPartial();
    }

    if (self) {
      var selfTemplate = messages.forSender(true, name);
      var selfMsg = format(selfTemplate, itemName, result);

      sender.sendSuccess(selfMsg);
      return;
    }

    var target = this.framework.actorOf(subject);
    var senderTemplate = messages.forSender(false, name);
    var targetTemplate = messages.forTarget(name);
    var selfMessage = format(senderTemplate, itemName, result);
    var targetMessage = format(targetTemplate, itemName, result);

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
      var invalidMsg = snap.invalidItem();
      sender.sendError(invalidMsg);
      return;
    }

    if (amount > snap.maxAmount()) {
      var tooLargeMsg = snap.formatAmountTooLarge();
      sender.sendError(tooLargeMsg);
      return;
    }

    var rawName = item.name();
    var itemName = rawName.toLowerCase(Locale.ROOT);
    var allPlayers = this.players.all();
    var count = 0;

    for (var player : allPlayers) {
      var delivered = deliverToOne(player, item, amount, itemName, snap);
      if (delivered) {
        count++;
      }
    }

    var givenAllMsg = snap.formatGivenAll(itemName, amount, count);
    sender.sendSuccess(givenAllMsg);
  }

  private boolean deliverToOne(
      @NonNull Player player,
      @NonNull Material item,
      int amount,
      @NonNull String itemName,
      @NonNull GiveConfig snap) {
    var result = this.service.giveResult(player, item, amount);
    var recipient = this.framework.actorOf(player);
    var name = player.getName();

    if (result.noneGiven()) {
      var fullPair = snap.whenInventoryFull();
      var fullTemplate = fullPair.forTarget(name);
      var fullMsg = format(fullTemplate, itemName, result);

      recipient.sendError(fullMsg);
      return false;
    }

    var messages = snap.whenGiven();
    if (result.partial()) {
      messages = snap.whenPartial();
    }

    var template = messages.forTarget(name);
    var givenMsg = format(template, itemName, result);

    recipient.sendSuccess(givenMsg);
    return true;
  }
}
