package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaffChatToggleServiceTest {

  @Test
  void isActiveReturnsFalseBeforeToggle() {
    var service = new StaffChatToggleService();
    var id = UUID.randomUUID();

    assertFalse(service.isActive(id));
  }

  @Test
  void toggleFlipsStateAndReportsNewValue() {
    var service = new StaffChatToggleService();
    var id = UUID.randomUUID();

    assertTrue(service.toggle(id));
    assertTrue(service.isActive(id));

    assertFalse(service.toggle(id));
    assertFalse(service.isActive(id));
  }

  @Test
  void clearRemovesActiveState() {
    var service = new StaffChatToggleService();
    var id = UUID.randomUUID();

    service.toggle(id);
    service.clear(id);

    assertFalse(service.isActive(id));
  }

  @Test
  void clearOnUnknownIdDoesNothing() {
    var service = new StaffChatToggleService();

    service.clear(UUID.randomUUID());
  }

  @Test
  void differentPlayersTrackIndependently() {
    var service = new StaffChatToggleService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.toggle(alice);

    assertTrue(service.isActive(alice));
    assertFalse(service.isActive(bob));
  }
}
