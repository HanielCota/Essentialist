package com.hanielcota.essentials.modules.trash.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import com.hanielcota.essentials.modules.trash.service.TrashService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command(value = "lixo", aliases = "trash")
@EssentialsCommand
@Permission("essentials.trash")
@Cooldown(duration = "3s")
@Description("Abre uma lixeira temporária para descartar itens.")
@Syntax("/lixo")
public record TrashCommand(ConfigHandle<TrashConfig> config, TrashService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Player player = actor.unwrap(Player.class);
    var snap = config.value();

    service.openTrash(player, snap.size(), snap.title());
  }
}
