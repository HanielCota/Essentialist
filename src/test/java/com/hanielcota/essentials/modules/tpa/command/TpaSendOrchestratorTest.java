package com.hanielcota.essentials.modules.tpa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.tpa.TpaTestSupport;
import com.hanielcota.essentials.modules.tpa.command.accept.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.accept.TpAcceptTeleportNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.repository.InMemoryRequestRepository;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import org.junit.jupiter.api.Test;

class TpaSendOrchestratorTest {

  @Test
  void autoAcceptFavoriteDoesNotSendStalePromptToTarget() {
    var requesterState = new TpaTestSupport.TestPlayerState("Alice");
    var targetState = new TpaTestSupport.TestPlayerState("Bob");
    var requester = TpaTestSupport.player(requesterState);
    var target = TpaTestSupport.player(targetState);
    var players = new TpaTestSupport.RecordingPlayerProvider();
    players.add(requester);
    players.add(target);
    var profiles = TpaTestSupport.profiles();
    profiles.toggleAutoAcceptFavorites(target.getUniqueId());
    var favorites = new TpaFavoriteService(null, NoopAsyncDatabaseWriter.INSTANCE);
    favorites.add(target.getUniqueId(), requester.getUniqueId(), requester.getName());
    var service =
        TpaTestSupport.service(
            new InMemoryRequestRepository(),
            new TpaTestSupport.RecordingHistory(),
            players,
            profiles,
            TpaTestSupport.blocks(),
            TpaTestSupport.contacts());
    var config = new TpaTestSupport.StaticConfigHandle();
    var actors = new TpaTestSupport.RecordingActorFactory();
    var acceptHandler =
        new TpAcceptOutcomeHandler(config, new TpaRequestReplyNotifier(actors, players));
    var teleportNotifier = new TpAcceptTeleportNotifier(config);
    var callbacks = new MainThreadCallbacks(new TpaTestSupport.DirectScheduler());
    var notifier = new TpaNotifier(config, players, profiles, favorites);
    var dispatcher =
        new TpaSendOrchestrator(
            config,
            service,
            favorites,
            profiles,
            acceptHandler,
            teleportNotifier,
            callbacks,
            actors,
            notifier);
    var requesterActor = actors.actorOf(requester);

    dispatcher.send(requesterActor, target, TeleportRequestType.TPA, "sent {player}");

    assertEquals(0, targetState.messages());
    assertEquals(0, targetState.sounds());
  }
}
