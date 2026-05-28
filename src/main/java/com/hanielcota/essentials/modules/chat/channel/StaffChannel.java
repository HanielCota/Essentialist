package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import com.hanielcota.essentials.modules.chat.service.StaffAudience;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

/**
 * Restricted broadcast — only players with {@link ChatPermissions#STAFF_RECEIVE} (plus the sender
 * and the console) see staff chat. Reached via {@code /staffchat} or by toggling persistent mode
 * through {@code /staffchat toggle}.
 */
public final class StaffChannel implements ChatChannel {

  public static final String ID = "staff";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public String template(@NonNull ChatConfig config) {
    return config.staff().format();
  }

  @Override
  public int cooldownSeconds(@NonNull ChatConfig config) {
    return config.staff().cooldownSeconds();
  }

  @Override
  public String bypassCooldownPermission() {
    return ChatPermissions.STAFF_BYPASS_COOLDOWN;
  }

  @Override
  public void filterViewers(@NonNull AsyncChatEvent event, @NonNull Player sender) {
    var senderId = sender.getUniqueId();
    event.viewers().removeIf(audience -> shouldRemove(audience, senderId));
  }

  private static boolean shouldRemove(@NonNull Audience audience, @NonNull UUID senderId) {
    if (!(audience instanceof Player viewer)) {
      return false;
    }

    return !StaffAudience.canHear(viewer, senderId);
  }
}
