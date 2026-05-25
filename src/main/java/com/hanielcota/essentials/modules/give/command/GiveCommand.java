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
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
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
    GiveNotifier notifier,
    PlayerProvider players) {

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

  @Subcommand({"all", "todos"})
  @Permission("essentials.give.all")
  @Description("Dá um item para todos os jogadores online.")
  @Syntax("/give all <item> [quantidade]")
  public void all(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    if (!validate(sender, item, amount)) {
      return;
    }

    var roster = this.players.all();
    var count =
        this.service.giveAll(
            roster,
            item,
            amount,
            (recipient, result) -> this.notifier.notifyRecipient(recipient, item, result));
    this.notifier.sendAllSummary(sender, item, amount, count);
  }

  private void deliver(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull Material item,
      int amount,
      boolean self) {
    if (!validate(sender, item, amount)) {
      return;
    }

    var result = this.service.giveResult(subject, item, amount);
    this.notifier.notifyDelivered(sender, subject, self, item, result);
  }

  private boolean validate(@NonNull CommandActor sender, @NonNull Material item, int amount) {
    if (!item.isItem()) {
      this.notifier.sendInvalidItem(sender);
      return false;
    }

    var snap = this.config.value();
    if (amount > snap.maxAmount()) {
      this.notifier.sendAmountTooLarge(sender);
      return false;
    }

    return true;
  }
}
