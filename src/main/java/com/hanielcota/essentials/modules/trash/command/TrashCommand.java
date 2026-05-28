package com.hanielcota.essentials.modules.trash.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "lixo", aliases = "trash")
@EssentialsCommand
@Permission("essentials.trash")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Abre uma lixeira temporária para descartar itens.")
@Syntax("/lixo")
public record TrashCommand(ConfigHandle<TrashConfig> config, TrashInventoryFactory factory) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var snap = this.config.value();
    var size = snap.size();
    var title = snap.title();

    var trash = this.factory.create(size, title);
    player.openInventory(trash);

    return CommandResult.success();
  }
}
