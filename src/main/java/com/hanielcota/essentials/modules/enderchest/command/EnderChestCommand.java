package com.hanielcota.essentials.modules.enderchest.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.enderchest.config.EnderChestConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "echest", aliases = "enderchest")
@EssentialsCommand
@Permission("essentials.echest")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Abre o seu Ender Chest ou o de outro jogador.")
@Syntax("/echest [jogador]")
public record EnderChestCommand(ConfigHandle<EnderChestConfig> config) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public CommandResult execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player target) {
    var viewer = sender.unwrap(Player.class);
    var snap = this.config.value();
    var self = target.equals(viewer);
    var targetName = target.getName();

    viewer.openInventory(target.getEnderChest());

    var messages = snap.whenOpened();
    var openedMsg = messages.forSender(self, targetName);

    sender.sendSuccess(openedMsg);

    return CommandResult.success();
  }
}
