package com.hanielcota.essentials.modules.god.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.god.config.GodConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** Sender/target feedback for {@code /god}, routing the dual enabled/disabled lines. */
@RequiredArgsConstructor
public final class GodNotifier {

  private final ConfigHandle<GodConfig> config;
  private final ActorFactory actors;

  public void announce(@NonNull CommandActor sender, @NonNull Player subject, boolean enabled) {
    var snap = this.config.value();
    var messages = snap.toggle(enabled);

    DualReply.send(sender, subject, this.actors, messages);
  }
}
