package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Pluggable pre-send check for a chat message. Implementations decide whether the sender may
 * publish {@code message} on {@code channel} and, when blocking, warn the sender themselves.
 *
 * <p>{@link #onPass} is called after the entire pipeline clears, giving each check a chance to
 * record state (cooldown, anti-spam) only when the message will actually be published. The pipeline
 * never needs to know which services individual checks touch.
 *
 * <p>Runs on the async chat thread when called from the listener; called on the main thread when
 * invoked from a command path. Implementations must be safe in both.
 */
public interface ChatGuardCheck {

  ChatGuardOutcome evaluate(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message);

  default void onPass(@NonNull String message, @NonNull UUID senderId, @NonNull String channelId) {}
}
