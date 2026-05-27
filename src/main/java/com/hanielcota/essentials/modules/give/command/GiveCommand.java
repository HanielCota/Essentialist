package com.hanielcota.essentials.modules.give.command;

import com.hanielcota.essentials.command.Senders;
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
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command("give")
@Permission("essentials.give")
@Cooldown(duration = "3s")
@Description("Dá itens a um jogador.")
@Syntax("/give <item> [quantidade] | /give para <jogador> <item> [quantidade]")
public record GiveCommand(GiveOrchestrator orchestrator, PlayerProvider players) {

  /**
   * Gives the caller (self) the item. CommandFramework parameter parsing is positional-strict and
   * does not backtrack: trying to fit both an optional amount and an optional target into the same
   * default subcommand made {@code /give DIAMOND Alice} unparsable (Alice fed to the int parser).
   * Self-only here, with an explicit {@code /give para} subcommand for the other-player case.
   */
  @DefaultSubcommand
  @PlayerOnly
  public CommandResult execute(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var subject = sender.unwrap(Player.class);

    this.orchestrator.deliver(sender, subject, item, amount, true);
    return CommandResult.success();
  }

  @Subcommand({"para", "to"})
  @Permission("essentials.give.others")
  @Description("Dá itens a outro jogador.")
  @Syntax("/give para <jogador> <item> [quantidade]")
  public CommandResult executeFor(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player subject,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var self = Senders.isSelf(sender, subject);

    this.orchestrator.deliver(sender, subject, item, amount, self);
    return CommandResult.success();
  }

  @Subcommand({"all", "todos"})
  @Permission("essentials.give.all")
  @Description("Dá um item para todos os jogadores online.")
  @Syntax("/give all <item> [quantidade]")
  public CommandResult all(
      @NonNull CommandActor sender,
      @Arg("item") Material item,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var roster = this.players.all();

    this.orchestrator.giveAll(sender, roster, item, amount);
    return CommandResult.success();
  }
}
