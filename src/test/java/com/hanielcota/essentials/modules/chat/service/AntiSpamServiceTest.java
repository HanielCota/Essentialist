package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AntiSpamServiceTest {

  @Test
  void isRepeatReturnsFalseBeforeAnyRecording() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    assertFalse(service.isRepeat(id, "hello"));
  }

  @Test
  void isRepeatReturnsTrueForExactDuplicate() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    service.record(id, "hello");

    assertTrue(service.isRepeat(id, "hello"));
  }

  @Test
  void isRepeatReturnsFalseForDifferentMessage() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    service.record(id, "hello");

    assertFalse(service.isRepeat(id, "world"));
  }

  @Test
  void isRepeatIsCaseSensitive() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    service.record(id, "Hello");

    assertFalse(service.isRepeat(id, "hello"));
  }

  @Test
  void recordOverwritesPreviousMessage() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    service.record(id, "first");
    service.record(id, "second");

    assertFalse(service.isRepeat(id, "first"));
    assertTrue(service.isRepeat(id, "second"));
  }

  @Test
  void clearRemovesTracking() {
    var service = new AntiSpamService();
    var id = UUID.randomUUID();

    service.record(id, "hello");
    service.clear(id);

    assertFalse(service.isRepeat(id, "hello"));
  }

  @Test
  void clearOnUnknownIdDoesNothing() {
    var service = new AntiSpamService();

    service.clear(UUID.randomUUID());
  }

  @Test
  void differentPlayersTrackIndependently() {
    var service = new AntiSpamService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.record(alice, "hello");
    service.record(bob, "world");

    assertTrue(service.isRepeat(alice, "hello"));
    assertTrue(service.isRepeat(bob, "world"));
    assertFalse(service.isRepeat(alice, "world"));
    assertFalse(service.isRepeat(bob, "hello"));
  }
}
