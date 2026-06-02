package com.hanielcota.essentials.modules.socialspy.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SocialSpyServiceTest {

  @Test
  void enterReturnsTrueForFirstAdd() {
    var service = new SocialSpyService();
    var id = UUID.randomUUID();

    assertTrue(service.enter(id));
    assertTrue(service.spies().contains(id));
  }

  @Test
  void enterReturnsFalseForDuplicate() {
    var service = new SocialSpyService();
    var id = UUID.randomUUID();

    service.enter(id);

    assertFalse(service.enter(id));
  }

  @Test
  void exitReturnsTrueForRemovingExisting() {
    var service = new SocialSpyService();
    var id = UUID.randomUUID();

    service.enter(id);

    assertTrue(service.exit(id));
    assertTrue(service.spies().isEmpty());
  }

  @Test
  void exitReturnsFalseForUnknown() {
    var service = new SocialSpyService();

    assertFalse(service.exit(UUID.randomUUID()));
  }

  @Test
  void differentPlayersTrackIndependently() {
    var service = new SocialSpyService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.enter(alice);

    assertTrue(service.spies().contains(alice));
    assertFalse(service.spies().contains(bob));
  }
}
