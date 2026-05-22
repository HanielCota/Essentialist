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
import java.util.Objects;
import net.kyori.adventure.text.Component;
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
      CommandActor sender, @DefaultValue("") @GreedyString @Arg("nome") String nome) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(nome, "nome");

    var snap = config.value();
    Player player = sender.unwrap(Player.class);
    String trimmed = nome.strip();

    // No name resets the item; a name is shown without the default italic styling.
    Component name =
        trimmed.isEmpty()
            ? null
            : ComponentUtils.mini(trimmed).decoration(TextDecoration.ITALIC, false);

    switch (service.rename(player, name)) {
      case RENAMED -> sender.sendSuccess(snap.formatRenamed(trimmed));
      case CLEARED -> sender.sendSuccess(snap.cleared());
      case EMPTY_HAND -> sender.sendError(snap.emptyHand());
      default -> throw new IllegalStateException("Unexpected rename result");
    }
  }
}
