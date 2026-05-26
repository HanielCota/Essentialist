package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Identifies a delivery destination for a chat message — who hears it, and what template formats
 * it. {@code sealed} so the {@link
 * com.hanielcota.essentials.modules.chat.listener.AsyncChatListener AsyncChatListener} switch over
 * channel kinds is exhaustively type-checked; adding a new channel means amending this hierarchy
 * and the router, never a stringly-typed lookup.
 *
 * <p>{@link #filterViewers} mutates {@link AsyncChatEvent#viewers()} on the async chat thread —
 * Paper documents this as thread-safe for the duration of the event. Implementations may read
 * player locations and permissions during the filter.
 */
public sealed interface ChatChannel permits GlobalChannel, LocalChannel, StaffChannel {

  String id();

  String template(@NonNull ChatConfig config);

  void filterViewers(@NonNull AsyncChatEvent event, @NonNull Player sender);

  /**
   * Called after viewer filtering. Returns {@code true} when the channel handled the "no audience"
   * case itself (e.g. local chat warning the sender) and the listener should cancel the event so
   * Paper does not broadcast a message that has only the sender as a viewer.
   */
  default boolean handleEmptyViewers(@NonNull Player sender, @NonNull AsyncChatEvent event) {
    return false;
  }
}
