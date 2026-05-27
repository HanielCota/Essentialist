package com.hanielcota.essentials.modules.rename.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.rename.config.RenameConfig;
import com.hanielcota.essentials.modules.rename.service.RenameService;
import com.hanielcota.essentials.shared.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

@Command("rename")
@EssentialsCommand
@Permission("essentials.rename")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Renomeia o item na mão; sem nome, remove o nome customizado.")
@Syntax("/rename [nome]")
public record RenameCommand(ConfigHandle<RenameConfig> config, RenameService service) {

  private static @Nullable Component renderName(@NonNull String trimmed) {
    if (trimmed.isEmpty()) {
      return null;
    }

    var base = ComponentUtils.mini(trimmed);
    return base.decoration(TextDecoration.ITALIC, false);
  }

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @GreedyString @Arg("nome") Optional<String> nome) {
    var snap = this.config.value();
    var player = sender.unwrap(Player.class);
    var trimmed = nome.map(String::strip).orElse("");

    var nameComponent = renderName(trimmed);
    var result = this.service.rename(player, nameComponent);

    return switch (result) {
      case RENAMED -> {
        var renamedMsg = snap.formatRenamed(trimmed);
        sender.sendSuccess(renamedMsg);
        yield CommandResult.success();
      }
      case CLEARED -> {
        var clearedMsg = snap.cleared();
        sender.sendSuccess(clearedMsg);
        yield CommandResult.success();
      }
      case EMPTY_HAND -> {
        var emptyHandMsg = snap.emptyHand();
        yield CommandResult.invalidUsage(emptyHandMsg);
      }
    };
  }
}
