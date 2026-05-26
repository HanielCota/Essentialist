package com.hanielcota.essentials.modules.trash.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command(value = "lixo", aliases = "trash")
@EssentialsCommand
@Permission("essentials.trash")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Abre uma lixeira temporária para descartar itens.")
@Syntax("/lixo")
public record TrashCommand(ConfigHandle<TrashConfig> config) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var snap = this.config.value();
    var size = snap.size();
    var title = snap.title();
    var titleComponent = ComponentUtils.mini(title);

    // No holder: nothing persists the inventory, so whatever is placed inside is discarded when
    // the menu closes.
    var trash = Bukkit.createInventory(null, size, titleComponent);
    player.openInventory(trash);
  }
}
