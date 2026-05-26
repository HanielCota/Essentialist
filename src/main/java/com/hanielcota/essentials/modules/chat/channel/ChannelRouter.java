package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Decides which {@link ChatChannel} consumes a message. Routing precedence:
 *
 * <ol>
 *   <li>Sender has persistent staff chat toggled on → {@link StaffChannel}.
 *   <li>Plain text starts with the configured global prefix → {@link GlobalChannel} (when the
 *       sender either has {@link ChatPermissions#GLOBAL_USE} or the channel does not require
 *       permission). When the permission is missing, the message falls through to local instead of
 *       being blocked — spec says "permissão opcional".
 *   <li>Default → {@link LocalChannel}.
 * </ol>
 */
@RequiredArgsConstructor
public final class ChannelRouter {

  private final ConfigHandle<ChatConfig> config;
  private final StaffChatToggleService staffToggle;
  private final GlobalChannel global;
  private final LocalChannel local;
  private final StaffChannel staff;

  public RoutedMessage route(@NonNull Player sender, @NonNull String plainMessage) {
    var senderId = sender.getUniqueId();
    if (this.staffToggle.isActive(senderId)) {
      return new RoutedMessage(this.staff, plainMessage);
    }

    var snap = this.config.value();
    var globalCfg = snap.global();
    var prefix = globalCfg.prefix();

    var routedToGlobal =
        !prefix.isEmpty()
            && plainMessage.startsWith(prefix)
            && (!globalCfg.requirePermission() || sender.hasPermission(ChatPermissions.GLOBAL_USE));

    if (routedToGlobal) {
      var stripped = plainMessage.substring(prefix.length()).stripLeading();
      return new RoutedMessage(this.global, stripped);
    }

    return new RoutedMessage(this.local, plainMessage);
  }
}
