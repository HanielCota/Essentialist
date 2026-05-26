package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Player-only entry point for the staff channel.
 *
 * <ul>
 *   <li>{@code /staffchat <message>} — sends a one-shot staff message via {@link
 *       StaffChatNotifier}.
 *   <li>{@code /staffchat toggle} — flips persistent staff mode; while active, every regular chat
 *       message routes through {@link com.hanielcota.essentials.modules.chat.channel.StaffChannel
 *       StaffChannel}.
 *   <li>{@code /staffchat} (no args) — prints usage.
 * </ul>
 *
 * <p>{@code @PlayerOnly} is required because the formatter needs a {@link Player} to resolve {@code
 * <player>} / {@code <world>} / {@code <displayname>} placeholders, and the toggle service keys on
 * a {@link java.util.UUID} that the console does not have.
 */
@Command(
    value = "staffchat",
    aliases = {"sc"})
@Permission(ChatPermissions.STAFF_USE)
@PlayerOnly
@Description("Send a staff-only chat message or toggle persistent staff mode.")
@Syntax("/staffchat <message | toggle>")
public record StaffChatCommand(StaffChatToggleService toggleService, StaffChatNotifier notifier) {

  @Subcommand("toggle")
  @Description("Toggle persistent staff chat for your session.")
  @Syntax("/staffchat toggle")
  public void toggle(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var id = player.getUniqueId();
    var nowActive = this.toggleService.toggle(id);

    if (nowActive) {
      this.notifier.sendToggleOn(actor);
      return;
    }

    this.notifier.sendToggleOff(actor);
  }

  @DefaultSubcommand
  public void send(
      @NonNull CommandActor actor, @GreedyString @DefaultValue("") @Arg("message") String message) {
    var body = message.strip();
    if (body.isEmpty()) {
      this.notifier.sendUsage(actor);
      return;
    }

    var player = actor.unwrap(Player.class);
    this.notifier.sendOneShot(player, body);
  }
}
