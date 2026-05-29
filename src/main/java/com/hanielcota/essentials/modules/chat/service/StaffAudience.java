package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import com.hanielcota.essentials.modules.chat.command.StaffChatNotifier;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Single domain rule for the staff-chat audience: the sender plus every online player carrying
 * {@link ChatPermissions#STAFF_RECEIVE}. Consumed by {@link StaffChannel} (to filter the regular
 * chat viewers list) and {@link StaffChatNotifier} (to fan a {@code /staffchat} broadcast out
 * directly).
 *
 * <p>Centralising the predicate means a permission change happens in exactly one place.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaffAudience {

  /** Whether {@code viewer} should hear a staff message authored by {@code senderId}. */
  public static boolean canHear(@NonNull Player viewer, @NonNull UUID senderId) {
    var viewerId = viewer.getUniqueId();
    if (viewerId.equals(senderId)) {
      return true;
    }
    return viewer.hasPermission(ChatPermissions.STAFF_RECEIVE);
  }
}
