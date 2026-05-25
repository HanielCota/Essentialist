package com.hanielcota.essentials.paper;

import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Default {@link ActorFactory} backed by {@link PaperCommandFramework#actorOf(Player)}. Bootstrap
 * registers a single instance; everything else injects the {@link ActorFactory} interface so the
 * collaborator graph stays decoupled from the command framework.
 */
@RequiredArgsConstructor
public final class FrameworkActorFactory implements ActorFactory {

  private final PaperCommandFramework framework;

  @Override
  public CommandActor actorOf(@NonNull Player player) {
    return this.framework.actorOf(player);
  }
}
