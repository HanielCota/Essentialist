package com.hanielcota.essentials.modules.tpa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.tpa.TpaTestSupport;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
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
    var favorites = new TpaFavoriteService(null, new TpaTestSupport.NoopWriter());
    favorites.add(target.getUniqueId(), requester.getUniqueId(), requester.getName());
    var service =
        TpaTestSupport.service(
            new RequestRepository(),
            new TpaTestSupport.RecordingHistory(),
            players,
            profiles,
            TpaTestSupport.blocks(),
            TpaTestSupport.contacts());
    var config = new TpaTestSupport.StaticConfigHandle();
    var actors = new TpaTestSupport.RecordingActorFactory();
    var acceptHandler =
        new TpAcceptOutcomeHandler(config, new TpaRequestReplyNotifier(actors, players));
    var callbacks = new MainThreadCallbacks(new TpaTestSupport.DirectScheduler());
    var dispatcher =
        new TpaSendOrchestrator(
            config, service, favorites, profiles, acceptHandler, callbacks, actors);
    var requesterActor = actors.actorOf(requester);

    dispatcher.send(requesterActor, target, TeleportRequestType.TPA, "sent {player}");

    assertEquals(0, targetState.messages());
    assertEquals(0, targetState.sounds());
  }
}
