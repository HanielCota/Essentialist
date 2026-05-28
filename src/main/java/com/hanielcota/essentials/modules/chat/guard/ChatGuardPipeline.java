package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
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
 * <p>After the chain clears, {@link ChatGuardCheck#onPass} is called on every check so each check
 * owns its own side-effects (cooldown, anti-spam). The pipeline is agnostic about which services
 * individual checks touch.
 */
@RequiredArgsConstructor
public final class ChatGuardPipeline {

  private final List<ChatGuardCheck> checks;

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

  public void onPass(@NonNull String message, @NonNull UUID senderId, @NonNull String channelId) {
    for (var check : this.checks) {
      check.onPass(message, senderId, channelId);
    }
  }
}
