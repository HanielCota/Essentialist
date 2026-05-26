package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.format.ChatFormatPipeline;
import com.hanielcota.essentials.modules.chat.format.PlayerMessageStyler;
import com.hanielcota.essentials.modules.chat.guard.ChatGuardPipeline;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * The single hot path of the chat module for messages typed into the regular chat box (anything
 * that does <em>not</em> go through {@code /g} or {@code /staffchat <message>}). Runs on the async
 * chat thread; everything inside this listener is allocation-conscious and avoids touching the main
 * thread.
 *
 * <p>The listener is intentionally thin — every non-trivial responsibility lives in a collaborator:
 *
 * <ul>
 *   <li>{@link ChannelRouter} — decides which channel consumes the message.
 *   <li>{@link ChatGuardPipeline} — cooldown + repeated-message + future checks.
 *   <li>{@link PlayerMessageStyler} — applies {@code chat.color}/{@code chat.format} permissions to
 *       the typed message.
 *   <li>{@link ChatFormatPipeline} — final MiniMessage render (parse → placeholders → resolver →
 *       render).
 * </ul>
 *
 * <p>Priority {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} so cancellers at
 * lower priorities (e.g. {@code MuteChatListener} at LOWEST) win and we do not waste a parse on a
 * silenced message.
 */
@RequiredArgsConstructor
public final class AsyncChatListener implements Listener {

  private static final PlainTextComponentSerializer PLAIN =
      PlainTextComponentSerializer.plainText();

  private final ConfigHandle<ChatConfig> config;
  private final ChannelRouter router;
  private final ChatFormatPipeline formatPipeline;
  private final ChatGuardPipeline guards;
  private final PlayerMessageStyler styler;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    var sender = event.getPlayer();
    var senderId = sender.getUniqueId();
    var typedMessage = event.message();
    var plainMessage = PLAIN.serialize(typedMessage);

    var routed = this.router.route(sender, plainMessage);
    var channel = routed.channel();
    var routedMessage = routed.message();

    if (this.guards.shouldBlock(sender, channel, routedMessage)) {
      event.setCancelled(true);
      return;
    }

    this.guards.touch(senderId, channel.id(), routedMessage);

    channel.filterViewers(event, sender);

    if (channel.handleEmptyViewers(sender, event)) {
      event.setCancelled(true);
      return;
    }

    var snap = this.config.value();
    var template = channel.template(snap);
    var messageComponent = this.styler.style(sender, routedMessage);

    event.renderer(
        ChatRenderer.viewerUnaware(
            (source, sourceDisplayName, ignored) ->
                this.formatPipeline.format(source, messageComponent, template)));
  }
}
