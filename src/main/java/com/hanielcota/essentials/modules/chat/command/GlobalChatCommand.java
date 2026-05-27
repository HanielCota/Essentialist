package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
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
import org.bukkit.entity.Player;

/**
 * Player-only entry point for the global channel.
 *
 * <ul>
 *   <li>{@code /g <message>} (alias {@code /global}) — broadcasts the message to every online
 *       player and the console using the configured global format.
 *   <li>{@code /g} with no message — prints usage.
 * </ul>
 *
 * <p>Players reach the global channel only by typing the command — there is no prefix-based path.
 * {@code @PlayerOnly} because {@link
 * com.hanielcota.essentials.modules.chat.format.ChatFormatPipeline ChatFormatPipeline} needs a
 * {@link Player} to resolve the {@code <player>} / {@code <world>} / {@code <displayname>}
 * placeholders.
 */
@Command(
    value = "g",
    aliases = {"global"})
@Permission(ChatPermissions.GLOBAL_USE)
@PlayerOnly
@Description("Send a global chat message.")
@Syntax("/g <message>")
public record GlobalChatCommand(GlobalChatNotifier notifier) {

  @DefaultSubcommand
  public CommandResult send(
      @NonNull CommandActor actor, @GreedyString @Arg("message") Optional<String> message) {
    var body = message.map(String::strip).orElse("");
    if (body.isEmpty()) {
      return CommandResult.invalidUsage(actor, "");
    }

    var player = actor.unwrap(Player.class);
    this.notifier.sendOneShot(player, body);
    return CommandResult.success();
  }
}
