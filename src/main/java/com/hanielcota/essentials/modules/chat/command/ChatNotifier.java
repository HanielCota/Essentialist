package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Renders and delivers messages for the {@code /chat} command. Pulled out of {@link ChatCommand} so
 * the command handler stays as a thin entry point and the formatting + delivery logic owns its
 * dependencies (config + actor factory).
 */
@RequiredArgsConstructor
public final class ChatNotifier {

  private final ConfigHandle<ChatConfig> config;
  private final ActorFactory actors;

  public void sendUsage(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();
    var usage = messages.usage();

    actor.sendMessage(usage);
  }

  public void sendReloadSuccess(@NonNull CommandActor actor, int total) {
    var snap = this.config.value();
    var messages = snap.messages();
    var line = messages.formatReloadSuccess(total);

    actor.sendSuccess(line);
  }

  public void sendReloadFailure(
      @NonNull CommandActor actor, int succeeded, int total, @NonNull String failed) {
    var snap = this.config.value();
    var messages = snap.messages();
    var line = messages.formatReloadFailure(succeeded, total, failed);

    actor.sendError(line);
  }
}
