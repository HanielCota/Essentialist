package com.hanielcota.essentials.modules.vanish.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.menu.VanishMenu;
import com.hanielcota.essentials.modules.vanish.service.VanishTransitions;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("vanish")
@EssentialsCommand
@Permission("essentials.vanish")
@Description("Toggles vanish for the sender or another player.")
@Syntax("/vanish [jogador] | /vanish list")
public record VanishCommand(
    ConfigHandle<VanishConfig> config,
    VanishTransitions transitions,
    ActorFactory actors,
    MenuService menus) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var newlyVanished = this.transitions.toggle(subject);
    var messages = snap.messages().toggle(newlyVanished);

    if (self) {
      var selfMsg = messages.forSender(true, name);
      sender.sendSuccess(selfMsg);

      return CommandResult.success();
    }

    var senderMsg = messages.forSender(false, name);
    var targetMsg = messages.forTarget(name);
    var target = this.actors.actorOf(subject);

    sender.sendDualMessage(target, senderMsg, targetMsg);

    return CommandResult.success();
  }

  @Subcommand("list")
  @PlayerOnly
  @Permission(VanishVisibilityApplier.SEE_PERMISSION)
  @Description("Opens the menu with every currently vanished player.")
  @Syntax("/vanish list")
  public CommandResult list(@NonNull CommandActor sender) {
    var player = sender.unwrap(Player.class);
    MenuOpenings.open(this.menus, player, VanishMenu.ID, sender);

    return CommandResult.success();
  }
}
