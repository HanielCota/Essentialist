package com.hanielcota.essentials.modules.fly.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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
  private final PaperCommandFramework framework;

  public void announce(
      @NonNull CommandActor sender, @NonNull Player subject, @NonNull FlyService.Result result) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (result == FlyService.Result.UNSUPPORTED) {
      var unsupported = snap.unsupportedGamemode();
      var unsupportedMsg = unsupported.forSender(self, name);
      sender.sendError(unsupportedMsg);
      return;
    }

    var enabled = result == FlyService.Result.ENABLED;
    var messages = snap.toggle(enabled);
    var target = this.framework.actorOf(subject);

    var senderMsg = messages.forSender(self, name);
    var targetMsg = messages.forTarget(name);

    sender.sendDualMessage(target, senderMsg, targetMsg);
  }
}
