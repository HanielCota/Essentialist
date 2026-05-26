package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * The single hot path of the chat module: intercept {@link AsyncChatEvent} and install a {@link
 * ChatRenderer#viewerUnaware ViewerUnaware} renderer. Paper invokes the renderer at most once per
 * message and reuses the resulting component for every recipient, so the cost of formatting scales
 * with sent messages, not with online player count.
 *
 * <p>Runs at {@link EventPriority#NORMAL} with {@code ignoreCancelled = true} so this listener
 * never fights cancellers that live at {@link EventPriority#LOWEST} (e.g. {@code MuteChatListener})
 * — if a mute has already cancelled the event, formatting it would be a wasted parse.
 */
@RequiredArgsConstructor
public final class AsyncChatListener implements Listener {

  private final ChatFormatter formatter;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    event.renderer(ChatRenderer.viewerUnaware(this::render));
  }

  private Component render(
      @NonNull Player source, @NonNull Component sourceDisplayName, @NonNull Component message) {
    return this.formatter.format(source, message);
  }
}
