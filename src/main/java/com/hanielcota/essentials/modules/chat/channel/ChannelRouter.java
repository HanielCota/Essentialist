package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Decides which {@link ChatChannel} consumes a regular chat message — i.e. anything typed into the
 * chat window without using {@code /g} or {@code /staffchat}. Two outcomes only:
 *
 * <ol>
 *   <li>Sender has persistent staff chat toggled on → {@link StaffChannel}.
 *   <li>Default → {@link LocalChannel}.
 * </ol>
 *
 * <p>The global channel is reached exclusively via the {@code /g} command; the listener never
 * routes there. Prefix-based routing (the previous {@code !} convention) was removed in PR 5 in
 * favour of explicit-command semantics.
 */
@RequiredArgsConstructor
public final class ChannelRouter {

  private final StaffChatToggleService staffToggle;
  private final LocalChannel local;
  private final StaffChannel staff;

  public RoutedMessage route(@NonNull Player sender, @NonNull String plainMessage) {
    var senderId = sender.getUniqueId();
    if (this.staffToggle.isActive(senderId)) {
      return new RoutedMessage(this.staff, plainMessage);
    }

    return new RoutedMessage(this.local, plainMessage);
  }
}
