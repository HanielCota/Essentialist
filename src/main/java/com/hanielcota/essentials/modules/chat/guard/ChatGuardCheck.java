package com.hanielcota.essentials.modules.chat.guard;

import com.hanielcota.essentials.modules.chat.channel.ChatChannel;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Pluggable pre-send check for a chat message. Implementations decide whether the sender may
 * publish {@code message} on {@code channel} and, when blocking, warn the sender themselves (so the
 * pipeline never owns presentation).
 *
 * <p>Runs on the async chat thread when called from {@link
 * com.hanielcota.essentials.modules.chat.listener.AsyncChatListener AsyncChatListener}; called on
 * the main thread when invoked from a command path. Implementations must be safe in both. The
 * pipeline ({@link ChatGuardPipeline}) iterates over checks in registration order and short-
 * circuits on the first {@link ChatGuardOutcome#BLOCK}.
 */
public interface ChatGuardCheck {

  ChatGuardOutcome evaluate(
      @NonNull Player sender, @NonNull ChatChannel channel, @NonNull String message);
}
