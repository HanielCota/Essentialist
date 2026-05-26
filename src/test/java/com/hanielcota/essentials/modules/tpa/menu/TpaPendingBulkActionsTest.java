package com.hanielcota.essentials.modules.tpa.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.tpa.TpaTestSupport;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class TpaPendingBulkActionsTest {

  @Test
  void acceptAllOnlyAcceptsNewestTpahereToAvoidTeleportingViewerRepeatedly() {
    var fixture = newFixture();
    var viewerState = new TpaTestSupport.TestPlayerState("Viewer");
    var firstRequesterState = new TpaTestSupport.TestPlayerState("Alice");
    var secondRequesterState = new TpaTestSupport.TestPlayerState("Bob");
    var viewer = TpaTestSupport.player(viewerState);
    var firstRequester = TpaTestSupport.player(firstRequesterState);
    var secondRequester = TpaTestSupport.player(secondRequesterState);
    fixture.players.add(viewer);
    fixture.players.add(firstRequester);
    fixture.players.add(secondRequester);
    fixture.service.create(firstRequester, viewer, TeleportRequestType.TPAHERE);
    fixture.service.create(secondRequester, viewer, TeleportRequestType.TPAHERE);

    fixture.bulk.acceptAll(TpaTestSupport.click(viewer));

    assertEquals(1, viewerState.teleports());
    assertEquals(1, fixture.service.incoming(viewer.getUniqueId()).size());
  }

  @Test
  void acceptAllLeavesTpaRequestsPendingWhenNewestTpahereIsAccepted() {
    var fixture = newFixture();
    var viewerState = new TpaTestSupport.TestPlayerState("Viewer");
    var tpaRequesterState = new TpaTestSupport.TestPlayerState("Alice");
    var tpaHereRequesterState = new TpaTestSupport.TestPlayerState("Bob");
    var viewer = TpaTestSupport.player(viewerState);
    var tpaRequester = TpaTestSupport.player(tpaRequesterState);
    var tpaHereRequester = TpaTestSupport.player(tpaHereRequesterState);
    fixture.players.add(viewer);
    fixture.players.add(tpaRequester);
    fixture.players.add(tpaHereRequester);
    fixture.service.create(tpaRequester, viewer, TeleportRequestType.TPA);
    fixture.service.create(tpaHereRequester, viewer, TeleportRequestType.TPAHERE);

    fixture.bulk.acceptAll(TpaTestSupport.click(viewer));

    assertEquals(1, viewerState.teleports());
    assertEquals(0, tpaRequesterState.teleports());
    assertEquals(1, fixture.service.incoming(viewer.getUniqueId()).size());
  }

  @Test
  void acceptAllNotifiesEveryAcceptedRequester() {
    var fixture = newFixture();
    var viewer = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Viewer"));
    var firstRequester = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Alice"));
    var secondRequester = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Bob"));
    fixture.players.add(viewer);
    fixture.players.add(firstRequester);
    fixture.players.add(secondRequester);
    fixture.service.create(firstRequester, viewer, TeleportRequestType.TPA);
    fixture.service.create(secondRequester, viewer, TeleportRequestType.TPA);

    fixture.bulk.acceptAll(TpaTestSupport.click(viewer));

    assertTrue(messages(fixture, firstRequester).stream().anyMatch(msg -> msg.contains("aceito")));
    assertTrue(messages(fixture, secondRequester).stream().anyMatch(msg -> msg.contains("aceito")));
  }

  @Test
  void denyAllNotifiesEveryDeniedRequester() {
    var fixture = newFixture();
    var viewer = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Viewer"));
    var firstRequester = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Alice"));
    var secondRequester = TpaTestSupport.player(new TpaTestSupport.TestPlayerState("Bob"));
    fixture.players.add(viewer);
    fixture.players.add(firstRequester);
    fixture.players.add(secondRequester);
    fixture.service.create(firstRequester, viewer, TeleportRequestType.TPA);
    fixture.service.create(secondRequester, viewer, TeleportRequestType.TPA);

    fixture.bulk.denyAll(TpaTestSupport.click(viewer));

    assertTrue(messages(fixture, firstRequester).stream().anyMatch(msg -> msg.contains("recusou")));
    assertTrue(
        messages(fixture, secondRequester).stream().anyMatch(msg -> msg.contains("recusou")));
  }

  private static java.util.List<String> messages(
      @NonNull Fixture fixture, @NonNull org.bukkit.entity.Player player) {
    var actor = fixture.actors.actor(player);
    if (actor == null) {
      return java.util.List.of();
    }
    return actor.messages();
  }

  private static Fixture newFixture() {
    var store = new RequestRepository();
    var history = new TpaTestSupport.RecordingHistory();
    var players = new TpaTestSupport.RecordingPlayerProvider();
    var profiles = TpaTestSupport.profiles();
    var service =
        TpaTestSupport.service(
            store, history, players, profiles, TpaTestSupport.blocks(), TpaTestSupport.contacts());
    var config = new TpaTestSupport.StaticConfigHandle();
    var actors = new TpaTestSupport.RecordingActorFactory();
    var replyNotifier = new TpaRequestReplyNotifier(actors, players);
    var acceptHandler = new TpAcceptOutcomeHandler(config, replyNotifier);
    var callbacks = new MainThreadCallbacks(new TpaTestSupport.DirectScheduler());
    var bulk =
        new TpaPendingBulkActions(config, service, acceptHandler, replyNotifier, callbacks, actors);
    return new Fixture(service, players, actors, bulk);
  }

  private record Fixture(
      com.hanielcota.essentials.modules.tpa.service.TeleportRequestService service,
      TpaTestSupport.RecordingPlayerProvider players,
      TpaTestSupport.RecordingActorFactory actors,
      TpaPendingBulkActions bulk) {}
}
