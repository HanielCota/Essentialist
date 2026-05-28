package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Blocks back-to-back duplicate messages. Owns both the check and the anti-spam side-effect — the
 * pipeline only iterates checks and calls {@link #onPass} after the full chain clears.
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

  @Override
  public void onPass(@NonNull String message, @NonNull UUID senderId, @NonNull String channelId) {
    this.antiSpam.record(senderId, message);
  }
}
