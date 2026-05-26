package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.GlobalChannel;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.format.ChatFormatPipeline;
import com.hanielcota.essentials.modules.chat.format.PlayerMessageStyler;
import com.hanielcota.essentials.modules.chat.guard.ChatGuardPipeline;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Owns the side-effects of {@code /g}: applies the same cooldown + anti-spam guards that the chat
 * listener uses, renders the message through {@link ChatFormatPipeline} with the global template,
 * and broadcasts via {@link AudienceProvider#broadcast()}. The command path bypasses {@link
 * io.papermc.paper.event.player.AsyncChatEvent}, so the chat pipeline's listeners (mute, format,
 * etc.) intentionally do not see the broadcast — admins who want to mute {@code /g} should add
 * {@code g} and {@code global} to the mute module's blocked-commands list.
 */
@RequiredArgsConstructor
public final class GlobalChatNotifier {

  private final ConfigHandle<ChatConfig> config;
  private final ChatFormatPipeline formatPipeline;
  private final PlayerMessageStyler styler;
  private final ChatGuardPipeline guards;
  private final GlobalChannel channel;
  private final AudienceProvider audiences;

  public void sendOneShot(@NonNull Player sender, @NonNull String body) {
    if (this.guards.shouldBlock(sender, this.channel, body)) {
      return;
    }

    var senderId = sender.getUniqueId();
    this.guards.touch(senderId, this.channel.id(), body);

    var snap = this.config.value();
    var template = this.channel.template(snap);
    var messageComponent = this.styler.style(sender, body);
    var rendered = this.formatPipeline.format(sender, messageComponent, template);

    var audience = this.audiences.broadcast();
    audience.sendMessage(rendered);
  }

  public void sendUsage(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();
    var usage = messages.usage();
    // /g borrows the generic chat usage line because the only thing to say is "supply a message".
    var component = renderOrEmpty(usage);

    actor.sendMessage(component);
  }

  private static Component renderOrEmpty(@NonNull String mini) {
    if (mini.isEmpty()) {
      return Component.empty();
    }
    return ComponentUtils.mini(mini);
  }
}
