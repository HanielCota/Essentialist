package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.config.AntiSpamConfig;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import com.hanielcota.essentials.modules.chat.service.PlayerMessageStyler;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
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
 *   <li>Serialise the typed message to plain text (needed to detect the global prefix and to seed
 *       anti-spam comparison).
 *   <li>Ask {@link ChannelRouter} which channel consumes it.
 *   <li>Enforce the channel cooldown — sender lacking the channel's bypass permission is rejected
 *       with a {@code {seconds}}-templated warning, the event is cancelled, we return early.
 *   <li>Run the repeated-message check (when {@link AntiSpamConfig#blockRepeated()} is enabled and
 *       the sender lacks {@link ChatPermissions#BYPASS_ANTISPAM}). Cancel + warn if repeated.
 *   <li>Touch the cooldown timestamp and record the message text — only after the guards pass, so a
 *       blocked message does not poison the next attempt's anti-spam comparison.
 *   <li>Let the channel filter {@link AsyncChatEvent#viewers()} (proximity, staff permission, ...).
 *   <li>If the channel decides nobody else can hear (local chat warning), cancel and return.
 *   <li>Style the player's message via {@link PlayerMessageStyler} — colours/decorations only if
 *       the sender has the respective permission, literal otherwise.
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
  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;
  private final ChannelRouter router;
  private final ChatFormatter formatter;
  private final CooldownService cooldowns;
  private final AntiSpamService antiSpam;
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

    var snap = this.config.value();
    var antiSpamCfg = snap.antiSpam();

    if (isOnCooldown(sender, senderId, channel, snap, antiSpamCfg)) {
      event.setCancelled(true);
      return;
    }

    if (isRepeated(sender, senderId, routedMessage, antiSpamCfg)) {
      event.setCancelled(true);
      return;
    }

    this.cooldowns.touch(senderId, channel.id());
    this.antiSpam.record(senderId, routedMessage);

    channel.filterViewers(event, sender);

    if (channel.handleEmptyViewers(sender, event)) {
      event.setCancelled(true);
      return;
    }

    var template = channel.template(snap);
    var messageComponent = this.styler.style(sender, routedMessage);

    event.renderer(
        ChatRenderer.viewerUnaware(
            (source, sourceDisplayName, ignored) ->
                this.formatter.format(source, messageComponent, template)));
  }

  private boolean isOnCooldown(
      @NonNull Player sender,
      @NonNull UUID senderId,
      @NonNull ChatChannel channel,
      @NonNull ChatConfig snap,
      @NonNull AntiSpamConfig antiSpamCfg) {
    var cooldownSeconds = channel.cooldownSeconds(snap);
    if (cooldownSeconds <= 0) {
      return false;
    }

    var bypassPermission = channel.bypassCooldownPermission();
    if (sender.hasPermission(bypassPermission)) {
      return false;
    }

    var remainingMs = this.cooldowns.remainingMillis(senderId, channel.id(), cooldownSeconds);
    if (remainingMs <= 0) {
      return false;
    }

    // Ceil(remainingMs / 1000) so a 1.2s remainder shows "2s", not "1s".
    var remainingSeconds = (remainingMs + 999L) / 1000L;
    var warning = antiSpamCfg.formatCooldownWarning(remainingSeconds);
    var component = MINI.deserialize(warning);

    sender.sendMessage(component);

    return true;
  }

  private boolean isRepeated(
      @NonNull Player sender,
      @NonNull UUID senderId,
      @NonNull String message,
      @NonNull AntiSpamConfig antiSpamCfg) {
    if (!antiSpamCfg.blockRepeated()) {
      return false;
    }
    if (sender.hasPermission(ChatPermissions.BYPASS_ANTISPAM)) {
      return false;
    }
    if (!this.antiSpam.isRepeat(senderId, message)) {
      return false;
    }

    var warning = antiSpamCfg.repeatedWarning();
    if (warning.isEmpty()) {
      return true;
    }

    var component = MINI.deserialize(warning);
    sender.sendMessage(component);

    return true;
  }
}
