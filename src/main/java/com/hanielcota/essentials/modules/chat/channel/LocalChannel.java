package com.hanielcota.essentials.modules.chat.channel;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Proximity-based chat — only players within {@link
 * com.hanielcota.essentials.modules.chat.config.LocalChannelConfig#radius radius} in the same world
 * hear the message. {@link ChatPermissions#LOCAL_BYPASS_RANGE} skips the filter so admins always
 * broadcast.
 *
 * <p>Distance check uses {@link Location#distanceSquared(Location)} against the pre-squared radius
 * — avoids a square root per viewer. The sender is always retained because Paper expects the
 * sender's own client to see what they typed.
 *
 * <p>If after filtering no other player remains, {@link #handleEmptyViewers} sends a configurable
 * warning to the sender and returns {@code true} so the listener cancels the broadcast.
 */
@RequiredArgsConstructor
public final class LocalChannel implements ChatChannel {

  public static final String ID = "local";

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;

  @Override
  public String id() {
    return ID;
  }

  @Override
  public String template(@NonNull ChatConfig snap) {
    return snap.local().format();
  }

  @Override
  public int cooldownSeconds(@NonNull ChatConfig snap) {
    return snap.local().cooldownSeconds();
  }

  @Override
  public String bypassCooldownPermission() {
    return ChatPermissions.LOCAL_BYPASS_COOLDOWN;
  }

  @Override
  public void filterViewers(@NonNull AsyncChatEvent event, @NonNull Player sender) {
    if (sender.hasPermission(ChatPermissions.LOCAL_BYPASS_RANGE)) {
      return;
    }

    var snap = this.config.value();
    var local = snap.local();
    var radiusSquared = local.radiusSquared();
    var senderLocation = sender.getLocation();
    var senderWorld = sender.getWorld();
    var senderId = sender.getUniqueId();

    event
        .viewers()
        .removeIf(
            audience ->
                shouldRemove(audience, senderId, senderWorld, senderLocation, radiusSquared));
  }

  @Override
  public boolean handleEmptyViewers(@NonNull Player sender, @NonNull AsyncChatEvent event) {
    if (hasAtLeastOneOtherPlayer(event, sender)) {
      return false;
    }

    var snap = this.config.value();
    var local = snap.local();
    var warning = local.noListenerWarning();
    if (warning.isEmpty()) {
      return true;
    }

    var component = MINI.deserialize(warning);
    sender.sendMessage(component);

    return true;
  }

  private static boolean shouldRemove(
      @NonNull Audience audience,
      @NonNull UUID senderId,
      @NonNull World senderWorld,
      @NonNull Location senderLocation,
      double radiusSquared) {
    if (!(audience instanceof Player viewer)) {
      return false;
    }

    var viewerId = viewer.getUniqueId();
    if (viewerId.equals(senderId)) {
      return false;
    }

    var viewerWorld = viewer.getWorld();
    if (viewerWorld != senderWorld) {
      return true;
    }

    var viewerLocation = viewer.getLocation();
    var distSq = viewerLocation.distanceSquared(senderLocation);

    return distSq > radiusSquared;
  }

  private static boolean hasAtLeastOneOtherPlayer(
      @NonNull AsyncChatEvent event, @NonNull Player sender) {
    var senderId = sender.getUniqueId();
    for (var audience : event.viewers()) {
      if (!(audience instanceof Player viewer)) {
        continue;
      }
      var viewerId = viewer.getUniqueId();
      if (!viewerId.equals(senderId)) {
        return true;
      }
    }
    return false;
  }
}
