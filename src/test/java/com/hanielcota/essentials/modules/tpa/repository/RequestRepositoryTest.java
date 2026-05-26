package com.hanielcota.essentials.modules.tpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.tpa.domain.Participant;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class RequestRepositoryTest {

  @Test
  void addThenIncomingForReturnsTheRequest() {
    var repo = new RequestRepository();
    var requester = new Participant(UUID.randomUUID(), "Alice");
    var target = new Participant(UUID.randomUUID(), "Bob");
    var request =
        TeleportRequest.open(requester, target, TeleportRequestType.TPA, Duration.ofMinutes(1));

    repo.add(request);

    var incoming = repo.incomingFor(target.id());
    assertEquals(1, incoming.size());
    assertEquals(request, incoming.get(0));
  }

  @Test
  void addingASecondRequestFromTheSameRequesterEvictsTheFirst() {
    var repo = new RequestRepository();
    var requester = new Participant(UUID.randomUUID(), "Alice");
    var firstTarget = new Participant(UUID.randomUUID(), "Bob");
    var secondTarget = new Participant(UUID.randomUUID(), "Carol");

    var first = request(requester, firstTarget);
    var second = request(requester, secondTarget);

    repo.add(first);
    repo.add(second);

    // First was evicted: byId no longer carries it, neither does the original target's incoming.
    assertTrue(repo.incomingFor(firstTarget.id()).isEmpty());
    assertEquals(second, repo.outgoingOf(requester.id()).orElseThrow());
    assertEquals(second, repo.incomingFor(secondTarget.id()).get(0));
  }

  @Test
  void removeReturnsFalseOnSecondCall() {
    var repo = new RequestRepository();
    var request = sampleRequest();
    repo.add(request);

    assertTrue(repo.delete(request));
    assertFalse(repo.delete(request));
  }

  @Test
  void incomingFromMatchesNameCaseInsensitively() {
    var repo = new RequestRepository();
    var requester = new Participant(UUID.randomUUID(), "Alice");
    var target = new Participant(UUID.randomUUID(), "Bob");
    repo.add(
        TeleportRequest.open(requester, target, TeleportRequestType.TPA, Duration.ofMinutes(1)));

    assertTrue(repo.incomingFrom(target.id(), "alice").isPresent());
    assertTrue(repo.incomingFrom(target.id(), "ALICE").isPresent());
    assertFalse(repo.incomingFrom(target.id(), "stranger").isPresent());
  }

  @Test
  void expiredAtListsOnlyRequestsPastTheirWindow() {
    var repo = new RequestRepository();
    var requester = new Participant(UUID.randomUUID(), "Alice");
    var target = new Participant(UUID.randomUUID(), "Bob");

    var shortLifeReq =
        TeleportRequest.open(requester, target, TeleportRequestType.TPA, Duration.ofMillis(1));
    repo.add(shortLifeReq);

    // Use a clearly-future cutoff so the short-lived request is unambiguously expired.
    var future = Instant.now().plusSeconds(60);

    var expired = repo.expiredAt(future);
    assertEquals(1, expired.size());
  }

  @Test
  void involvingFindsBothRequesterAndTargetRoles() {
    var repo = new RequestRepository();
    var alice = new Participant(UUID.randomUUID(), "Alice");
    var bob = new Participant(UUID.randomUUID(), "Bob");
    var carol = new Participant(UUID.randomUUID(), "Carol");

    repo.add(request(alice, bob));
    repo.add(request(carol, bob));

    assertEquals(2, repo.involving(bob.id()).size());
    assertEquals(1, repo.involving(alice.id()).size());
  }

  private static TeleportRequest request(
      @NonNull Participant requester, @NonNull Participant target) {
    return TeleportRequest.open(requester, target, TeleportRequestType.TPA, Duration.ofMinutes(1));
  }

  private static TeleportRequest sampleRequest() {
    return request(
        new Participant(UUID.randomUUID(), "Alice"), new Participant(UUID.randomUUID(), "Bob"));
  }
}
