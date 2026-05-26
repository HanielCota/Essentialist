package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Blocks messages sent before the channel cooldown elapses. Bypass permission is whatever the
 * channel declares via {@link ChatChannel#bypassCooldownPermission()} — adding a channel needs no
 * change here.
 *
 * <p>The check warns the sender with the configured MiniMessage line on {@link
 * ChatGuardOutcome#BLOCK}; the cooldown timer itself is touched by the pipeline only after the full
 * chain returns {@code ALLOW}, so a blocked attempt never shifts the window.
 */
@RequiredArgsConstructor
public final class CooldownCheck implements ChatGuardCheck {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;
  private final CooldownService cooldowns;

  @Override
  public ChatGuardOutcome evaluate(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message) {
    var snap = this.config.value();
    var cooldownSeconds = channel.cooldownSeconds(snap);
    if (cooldownSeconds <= 0) {
      return ChatGuardOutcome.ALLOW;
    }

    var bypassPermission = channel.bypassCooldownPermission();
    if (sender.hasPermission(bypassPermission)) {
      return ChatGuardOutcome.ALLOW;
    }

    var senderId = sender.getUniqueId();
    var remainingMs = this.cooldowns.remainingMillis(senderId, channel.id(), cooldownSeconds);
    if (remainingMs <= 0) {
      return ChatGuardOutcome.ALLOW;
    }

    // Ceil(remainingMs / 1000) so a 1.2s remainder shows "2s", not "1s".
    var remainingSeconds = (remainingMs + 999L) / 1000L;
    var antiSpamCfg = snap.antiSpam();
    var warning = antiSpamCfg.formatCooldownWarning(remainingSeconds);
    var component = MINI.deserialize(warning);

    sender.sendMessage(component);

    return ChatGuardOutcome.BLOCK;
  }
}
