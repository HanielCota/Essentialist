package com.hanielcota.essentials.modules.invsee.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;
import org.bukkit.entity.Player;

@Command("invsee")
@EssentialsCommand
@Permission("essentials.invsee")
@Cooldown(duration = "3s")
@Description("Abre o inventário de outro jogador.")
@Syntax("/invsee <jogador>")
public record InvseeCommand(ConfigHandle<InvseeConfig> config) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @OnlinePlayer Player target) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(target, "target");

    Player viewer = sender.unwrap(Player.class);
    var snap = config.value();
    if (target.equals(viewer)) {
      sender.sendError(snap.self());
      return;
    }

    // Opens the target's live inventory — edits affect the real inventory.
    viewer.openInventory(target.getInventory());
    sender.sendSuccess(snap.formatOpened(target.getName()));
  }
}
