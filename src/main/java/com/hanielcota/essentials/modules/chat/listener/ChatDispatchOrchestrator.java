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

/**
 * Sequences the post-route stages of a chat event: route → guards → viewer filter → empty-viewer
 * handling → render. Extracted from {@link AsyncChatListener} so the listener stays a one-liner
 * delegate and the dispatch pipeline can be unit tested without {@code AsyncChatEvent} construction
 * tricks.
 */
@RequiredArgsConstructor
public final class ChatDispatchOrchestrator {

  private static final PlainTextComponentSerializer PLAIN =
      PlainTextComponentSerializer.plainText();

  private final @NonNull ConfigHandle<ChatConfig> config;
  private final @NonNull ChannelRouter router;
  private final @NonNull ChatFormatPipeline formatPipeline;
  private final @NonNull ChatGuardPipeline guards;
  private final @NonNull PlayerMessageStyler styler;

  public void dispatch(@NonNull AsyncChatEvent event) {
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

    this.guards.onPass(routedMessage, senderId, channel.id());

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
