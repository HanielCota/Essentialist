package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Runs every registered {@link ChatGuardCheck} in order and short-circuits on the first {@link
 * ChatGuardOutcome#BLOCK}. Adding a new check (caps filter, slow mode, mute lookup, profanity
 * filter…) means appending to the list at wiring time — no edits here.
 *
 * <p>{@link #touch} updates the cooldown + last-message records after the chain clears, so a
 * blocked attempt never shifts the cooldown window nor poisons the next anti-spam comparison. Touch
 * is intentionally outside the chain: the chain is "may this proceed?", touch is "we are about to
 * publish — record it".
 */
@RequiredArgsConstructor
public final class ChatGuardPipeline {

  private final List<ChatGuardCheck> checks;
  private final CooldownService cooldowns;
  private final AntiSpamService antiSpam;

  public boolean shouldBlock(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message) {
    for (var check : this.checks) {
      var outcome = check.evaluate(sender, channel, message);
      if (outcome == ChatGuardOutcome.BLOCK) {
        return true;
      }
    }
    return false;
  }

  public void touch(@NonNull UUID senderId, @NonNull String channelId, @NonNull String message) {
    this.cooldowns.touch(senderId, channelId);
    this.antiSpam.record(senderId, message);
  }
}
