package com.hanielcota.essentials.modules.itemlore.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.itemlore.config.ItemLoreConfig;
import com.hanielcota.essentials.modules.itemlore.service.ItemLoreService;
import com.hanielcota.essentials.shared.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Min;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

@Command("itemlore")
@EssentialsCommand
@PlayerOnly
@Permission("essentials.itemlore")
@Description("Edita a lore do item na mão.")
@Syntax("/itemlore add <texto> | set <linha> <texto> | remove <linha> | clear")
public record ItemLoreCommand(ConfigHandle<ItemLoreConfig> config, ItemLoreService service) {

  private static Component renderLine(@NonNull String text) {
    var base = ComponentUtils.mini(text);

    return base.decoration(TextDecoration.ITALIC, false);
  }

  @DefaultSubcommand
  public CommandResult usage(@NonNull CommandActor actor) {
    var snap = this.config.value();

    return CommandResult.invalidUsage(snap.usage());
  }

  @Subcommand("add")
  public CommandResult add(@NonNull CommandActor actor, @GreedyString @Arg("texto") String text) {
    var player = actor.unwrap(Player.class);
    var line = renderLine(text);

    var result = this.service.add(player, line);
    return respond(actor, result);
  }

  @Subcommand("set")
  public CommandResult set(
      @NonNull CommandActor actor,
      @Min(1) @Arg("linha") int line,
      @GreedyString @Arg("texto") String text) {
    var player = actor.unwrap(Player.class);
    var component = renderLine(text);

    var result = this.service.set(player, line, component);
    return respond(actor, result);
  }

  @Subcommand({"remove", "delete"})
  public CommandResult remove(@NonNull CommandActor actor, @Min(1) @Arg("linha") int line) {
    var player = actor.unwrap(Player.class);

    var result = this.service.remove(player, line);
    return respond(actor, result);
  }

  @Subcommand("clear")
  public CommandResult clear(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);

    var result = this.service.clear(player);
    return respond(actor, result);
  }

  private CommandResult respond(
      @NonNull CommandActor actor, @NonNull ItemLoreService.Result result) {
    var snap = this.config.value();

    return switch (result) {
      case ADDED -> success(actor, snap.added());
      case UPDATED -> success(actor, snap.updated());
      case REMOVED -> success(actor, snap.removed());
      case CLEARED -> success(actor, snap.cleared());
      case EMPTY_HAND -> CommandResult.invalidUsage(snap.emptyHand());
      case INVALID_LINE -> CommandResult.invalidUsage(snap.invalidLine());
      case EMPTY_LORE -> CommandResult.invalidUsage(snap.emptyLore());
    };
  }

  private static CommandResult success(@NonNull CommandActor actor, @NonNull String message) {
    actor.sendSuccess(message);

    return CommandResult.success();
  }
}
