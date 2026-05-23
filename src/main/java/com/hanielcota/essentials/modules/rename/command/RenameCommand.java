package com.hanielcota.essentials.modules.rename.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.rename.config.RenameConfig;
import com.hanielcota.essentials.modules.rename.service.RenameService;
import com.hanielcota.essentials.util.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

@Command("rename")
@EssentialsCommand
@Permission("essentials.rename")
@Cooldown(duration = "3s")
@Description("Renomeia o item na mão; sem nome, remove o nome customizado.")
@Syntax("/rename [nome]")
public record RenameCommand(ConfigHandle<RenameConfig> config, RenameService service) {

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender, @DefaultValue("") @GreedyString @Arg("nome") String nome) {
    var snap = this.config.value();
    var player = sender.unwrap(Player.class);
    var trimmed = nome.strip();

    var nameComponent =
        Optional.of(trimmed)
            .filter(str -> !str.isEmpty())
            .map(ComponentUtils::mini)
            .map(component -> component.decoration(TextDecoration.ITALIC, false))
            .orElse(null);

    var result = this.service.rename(player, nameComponent);

    switch (result) {
      case RENAMED -> {
        var renamedMsg = snap.formatRenamed(trimmed);
        sender.sendSuccess(renamedMsg);
      }
      case CLEARED -> sender.sendSuccess(snap.cleared());
      case EMPTY_HAND -> sender.sendError(snap.emptyHand());
    }
  }
}
