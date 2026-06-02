package com.hanielcota.essentials.modules.fly.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Sender/target feedback for {@code /fly} (toggle/on/off). Surfaces the unsupported-gamemode error
 * to the sender and routes the dual enabled/disabled lines on success.
 */
@RequiredArgsConstructor
public final class FlyNotifier {

  private final ConfigHandle<FlyConfig> config;
  private final ActorFactory actors;

  public void announce(
      @NonNull CommandActor sender, @NonNull Player subject, @NonNull FlyService.Result result) {
    var snap = this.config.value();

    if (result == FlyService.Result.UNSUPPORTED) {
      var name = subject.getName();
      var self = Senders.isSelf(sender, subject);
      var unsupported = snap.unsupportedGamemode();
      var unsupportedMsg = unsupported.forSender(self, name);
      sender.sendError(unsupportedMsg);
      return;
    }

    var enabled = result == FlyService.Result.ENABLED;
    var messages = snap.toggle(enabled);

    DualReply.send(sender, subject, this.actors, messages);
  }
}
