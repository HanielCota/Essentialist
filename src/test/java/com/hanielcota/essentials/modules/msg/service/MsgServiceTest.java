package com.hanielcota.essentials.modules.msg.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MsgServiceTest {

  @Test
  void lastPartnerReturnsEmptyBeforeAnyPairing() {
    var service = new MsgService();
    var id = UUID.randomUUID();

    assertFalse(service.lastPartner(id).isPresent());
  }

  @Test
  void pairLinksBothDirections() {
    var service = new MsgService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.pair(alice, bob);

    assertEquals(bob, service.lastPartner(alice).orElseThrow());
    assertEquals(alice, service.lastPartner(bob).orElseThrow());
  }

  @Test
  void pairOverwritesPreviousPartner() {
    var service = new MsgService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();
    var charlie = UUID.randomUUID();

    service.pair(alice, bob);
    service.pair(alice, charlie);

    assertEquals(charlie, service.lastPartner(alice).orElseThrow());
    assertEquals(alice, service.lastPartner(charlie).orElseThrow());
  }

  @Test
  void forgetRemovesLinkInOneDirectionOnly() {
    var service = new MsgService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.pair(alice, bob);
    service.forget(alice);

    assertFalse(service.lastPartner(alice).isPresent());
    assertEquals(alice, service.lastPartner(bob).orElseThrow());
  }

  @Test
  void forgetOnUnknownIdDoesNothing() {
    var service = new MsgService();

    service.forget(UUID.randomUUID());
  }
}
