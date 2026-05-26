package com.hanielcota.essentials.modules.tpa.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.tpa.TpaTestSupport;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.repository.InMemoryRequestRepository;
import org.junit.jupiter.api.Test;

class TpDenyCommandTest {

  @Test
  void executeReportsNoIncomingWhenRequestWasAlreadyResolved() {
    var viewer = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Viewer"));
    var requester = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Alice"));
    var players = new TpaTestSupport.RecordingPlayerProvider();
    players.add(viewer);
    players.add(requester);
    var service =
        TpaTestSupport.service(
            new InMemoryRequestRepository(),
            new TpaTestSupport.RecordingHistory(),
            players,
            TpaTestSupport.profiles(),
            TpaTestSupport.blocks(),
            TpaTestSupport.contacts());
    var request = service.create(requester, viewer, TeleportRequestType.TPA).orElseThrow();
    service.cancel(request);
    var actors = new TpaTestSupport.RecordingActorFactory();
    var actor = actors.actorOf(viewer);
    var resolver = new TpaIncomingResolver(new TpaTestSupport.StaticConfigHandle(), service);
    var command =
        new TpDenyCommand(
            new TpaTestSupport.StaticConfigHandle(),
            service,
            resolver,
            new TpaRequestReplyNotifier(actors, players));

    command.execute(actor, requester.getName());

    assertTrue(actors.actor(viewer).messages().stream().anyMatch(msg -> msg.contains("nenhum")));
  }
}
