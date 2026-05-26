package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * The single hot path of the chat module. Runs on the async chat thread; everything inside this
 * listener is allocation-conscious and avoids touching the main thread.
 *
 * <p>Flow per message:
 *
 * <ol>
 *   <li>Serialise the typed message to plain text (needed to detect the global prefix).
 *   <li>Ask {@link ChannelRouter} which channel consumes it.
 *   <li>Let the channel filter {@link AsyncChatEvent#viewers()} (proximity, staff permission, ...).
 *   <li>If the channel decides nobody else can hear (local chat warning), cancel and return.
 *   <li>Install a {@link ChatRenderer#viewerUnaware ViewerUnaware} renderer so Paper formats the
 *       component once and reuses it for every viewer.
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

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    var sender = event.getPlayer();
    var typedMessage = event.message();
    var plainMessage = PLAIN.serialize(typedMessage);

    var routed = this.router.route(sender, plainMessage);
    var channel = routed.channel();
    var routedMessage = routed.message();

    channel.filterViewers(event, sender);

    if (channel.handleEmptyViewers(sender, event)) {
      event.setCancelled(true);
      return;
    }

    var snap = this.config.value();
    var template = channel.template(snap);
    var messageComponent = messageComponentFor(typedMessage, plainMessage, routedMessage);

    event.renderer(
        ChatRenderer.viewerUnaware(
            (source, sourceDisplayName, ignored) ->
                this.formatter.format(source, messageComponent, template)));
  }

  /**
   * Returns the component we feed into the formatter. When the router stripped a prefix, we fall
   * back to a plain text component of the stripped body — the original typed component would still
   * carry the {@code !}. When nothing was stripped, we keep the typed component as-is so future
   * styling (PR 4, {@code chat.color}) flows through untouched.
   */
  private static Component messageComponentFor(
      @NonNull Component typedMessage,
      @NonNull String plainMessage,
      @NonNull String routedMessage) {
    if (routedMessage.equals(plainMessage)) {
      return typedMessage;
    }

    return Component.text(routedMessage);
  }
}
