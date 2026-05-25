package com.hanielcota.essentials.modules.light.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.light.config.LightConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Dual sender/target feedback for {@code /luz} (toggle/on/off). Maps the enabled flag to the
 * configured message pair and routes via {@link CommandActor#sendDualMessage}.
 */
@RequiredArgsConstructor
public final class LightNotifier {

  private final ConfigHandle<LightConfig> config;
  private final ActorFactory actors;

  public void announce(@NonNull CommandActor sender, @NonNull Player subject, boolean enabled) {
    var snap = this.config.value();
    var messages = snap.toggle(enabled);
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var target = this.actors.actorOf(subject);
    var selfMessage = messages.forSender(self, name);
    var targetMessage = messages.forTarget(name);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
