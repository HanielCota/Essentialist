package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import com.hanielcota.essentials.modules.chat.service.ChatGuard;
import com.hanielcota.essentials.modules.chat.service.PlayerMessageStyler;
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
 * <p>Flow per message:
 *
 * <ol>
 *   <li>Serialise the typed message to plain text (needed to seed anti-spam comparison).
 *   <li>Ask {@link ChannelRouter} which channel consumes it — either {@link
 *       com.hanielcota.essentials.modules.chat.channel.StaffChannel StaffChannel} (when the sender
 *       has persistent staff toggle active) or {@link
 *       com.hanielcota.essentials.modules.chat.channel.LocalChannel LocalChannel}.
 *   <li>Run {@link ChatGuard#shouldBlock} — cooldown + repeated-message checks live there. The
 *       guard already sends the configured warning to the sender on a block, so the listener just
 *       cancels and returns.
 *   <li>Touch the guard state — only after the message clears the checks, so a blocked attempt does
 *       not poison the next message's anti-spam comparison.
 *   <li>Let the channel filter {@link AsyncChatEvent#viewers()} (proximity, staff permission).
 *   <li>If the channel handles the "nobody else hears" case (local chat warning), cancel and
 *       return.
 *   <li>Style the player's message via {@link PlayerMessageStyler} (gated by {@code chat.color} /
 *       {@code chat.format}), then install a {@link ChatRenderer#viewerUnaware ViewerUnaware}
 *       renderer so Paper formats the component once per message and reuses it for every viewer.
 * </ol>
 *
 * <p>Priority {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} so cancellers at
 * lower priorities (e.g. {@code MuteChatListener} at LOWEST) win and we don't waste a parse on a
 * silenced message.
 */
@RequiredArgsConstructor
public final class AsyncChatListener implements Listener {

  private static final PlainTextComponentSerializer PLAIN =
      PlainTextComponentSerializer.plainText();

  private final ConfigHandle<ChatConfig> config;
  private final ChannelRouter router;
  private final ChatFormatter formatter;
  private final ChatGuard guard;
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

    if (this.guard.shouldBlock(sender, channel, routedMessage)) {
      event.setCancelled(true);
      return;
    }

    this.guard.touch(senderId, channel.id(), routedMessage);

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
                this.formatter.format(source, messageComponent, template)));
  }
}
