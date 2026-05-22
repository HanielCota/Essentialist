package com.hanielcota.essentials.modules.trash.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command(value = "lixo", aliases = "trash")
@EssentialsCommand
@Permission("essentials.trash")
@Cooldown(duration = "3s")
@Description("Abre uma lixeira temporária para descartar itens.")
@Syntax("/lixo")
public record TrashCommand(ConfigHandle<TrashConfig> config) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Objects.requireNonNull(actor, "actor");

    Player player = actor.unwrap(Player.class);
    var snap = config.value();

    // A standalone inventory with no holder: when the player closes it, nothing
    // persists it, so whatever was placed inside is discarded.
    Inventory trash = Bukkit.createInventory(null, snap.size(), ComponentUtils.mini(snap.title()));
    player.openInventory(trash);
  }
}
