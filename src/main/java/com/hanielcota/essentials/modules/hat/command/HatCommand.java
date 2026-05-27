package com.hanielcota.essentials.modules.hat.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.hat.config.HatConfig;
import com.hanielcota.essentials.modules.hat.service.HatService;
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

@Command(value = "chapeu", aliases = "hat")
@EssentialsCommand
@Permission("essentials.hat")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Equipa o item na mão como chapéu.")
@Syntax("/chapeu")
public record HatCommand(ConfigHandle<HatConfig> config, HatService service) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    Player sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var result = this.service.equip(sender, snap);

    return switch (result) {
      case EQUIPPED -> {
        actor.sendSuccess(snap.equipped());
        yield CommandResult.success();
      }
      case EMPTY_HAND -> CommandResult.invalidUsage(snap.emptyHand());
      case NOT_ALLOWED -> CommandResult.invalidUsage(snap.notAllowed());
      case INVENTORY_FULL -> CommandResult.invalidUsage(snap.inventoryFull());
    };
  }
}
