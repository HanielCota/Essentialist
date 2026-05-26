package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.format.ChatFormatPipeline;
import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Owns the side-effects of {@code /staffchat}: rendering the message through {@link
 * ChatFormatPipeline} with the configured staff template, then fanning it out to every online
 * player carrying {@link ChatPermissions#STAFF_RECEIVE} plus the console and the sender themselves.
 *
 * <p>The command path runs on the main thread (Bukkit dispatches commands on main), so iterating
 * {@link PlayerProvider#all()} and calling {@code hasPermission} is safe. We bypass {@code
 * AsyncChatEvent} entirely — this is a targeted broadcast, not regular chat, so the chat pipeline's
 * other listeners (mute, format, etc.) intentionally do not see it.
 */
@RequiredArgsConstructor
public final class StaffChatNotifier {

  private final ConfigHandle<ChatConfig> config;
  private final ChatFormatPipeline formatPipeline;
  private final PlayerProvider players;
  private final AudienceProvider audiences;

  public void sendOneShot(@NonNull Player sender, @NonNull String body) {
    var snap = this.config.value();
    var staff = snap.staff();
    var template = staff.format();
    var messageComponent = Component.text(body);
    var rendered = this.formatPipeline.format(sender, messageComponent, template);

    deliver(rendered, sender);
  }

  public void sendToggleOn(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var staff = snap.staff();
    var msg = staff.toggleOn();

    actor.sendMessage(msg);
  }

  public void sendToggleOff(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var staff = snap.staff();
    var msg = staff.toggleOff();

    actor.sendMessage(msg);
  }

  public void sendUsage(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var staff = snap.staff();
    var msg = staff.usage();

    actor.sendMessage(msg);
  }

  public void sendError(@NonNull CommandActor actor, @NonNull String mini) {
    var component = ComponentUtils.mini(mini);
    actor.sendMessage(component);
  }

  private void deliver(@NonNull Component rendered, @NonNull Player sender) {
    var senderId = sender.getUniqueId();
    var console = this.audiences.console();

    console.sendMessage(rendered);
    sender.sendMessage(rendered);

    var online = this.players.all();
    for (var viewer : online) {
      var viewerId = viewer.getUniqueId();
      if (viewerId.equals(senderId)) {
        continue;
      }
      if (!viewer.hasPermission(ChatPermissions.STAFF_RECEIVE)) {
        continue;
      }

      viewer.sendMessage(rendered);
    }
  }
}
