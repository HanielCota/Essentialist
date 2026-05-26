package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.config.AntiSpamConfig;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Channel-agnostic cooldown + repeated-message guard. Extracted from {@code AsyncChatListener} so
 * the {@code /g} command path can reuse the same checks without duplicating logic; both call sites
 * end up running through identical config-driven rules.
 *
 * <p>{@link #shouldBlock} is "check + warn the sender" — it returns {@code true} when the message
 * must not proceed and side-effects the sender with the configured MiniMessage warning. {@link
 * #touch} updates both backing services after a message clears the guards, so a blocked attempt
 * never poisons the next message's anti-spam comparison or shifts the cooldown window.
 */
@RequiredArgsConstructor
public final class ChatGuard {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;
  private final CooldownService cooldowns;
  private final AntiSpamService antiSpam;

  public boolean shouldBlock(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message) {
    var senderId = sender.getUniqueId();
    var snap = this.config.value();
    var antiSpamCfg = snap.antiSpam();

    if (isOnCooldown(sender, senderId, channel, snap, antiSpamCfg)) {
      return true;
    }
    if (isRepeated(sender, senderId, message, antiSpamCfg)) {
      return true;
    }

    return false;
  }

  public void touch(@NonNull UUID senderId, @NonNull String channelId, @NonNull String message) {
    this.cooldowns.touch(senderId, channelId);
    this.antiSpam.record(senderId, message);
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
