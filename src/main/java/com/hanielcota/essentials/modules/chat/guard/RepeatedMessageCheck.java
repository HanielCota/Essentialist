package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.permission.ChatPermissions;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Blocks back-to-back duplicate messages. The comparison is exact (case + whitespace) and only runs
 * when {@link com.hanielcota.essentials.modules.chat.config.AntiSpamConfig#blockRepeated() the
 * feature is enabled}. {@link ChatPermissions#BYPASS_ANTISPAM} skips the check entirely.
 *
 * <p>An empty {@code repeatedWarning} suppresses the warning component while still blocking —
 * useful when admins want a silent drop.
 */
@RequiredArgsConstructor
public final class RepeatedMessageCheck implements ChatGuardCheck {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;
  private final AntiSpamService antiSpam;

  @Override
  public ChatGuardOutcome evaluate(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message) {
    var snap = this.config.value();
    var antiSpamCfg = snap.antiSpam();
    if (!antiSpamCfg.blockRepeated()) {
      return ChatGuardOutcome.ALLOW;
    }

    if (sender.hasPermission(ChatPermissions.BYPASS_ANTISPAM)) {
      return ChatGuardOutcome.ALLOW;
    }

    var senderId = sender.getUniqueId();
    if (!this.antiSpam.isRepeat(senderId, message)) {
      return ChatGuardOutcome.ALLOW;
    }

    var warning = antiSpamCfg.repeatedWarning();
    if (warning.isEmpty()) {
      return ChatGuardOutcome.BLOCK;
    }

    var component = MINI.deserialize(warning);
    sender.sendMessage(component);

    return ChatGuardOutcome.BLOCK;
  }
}
