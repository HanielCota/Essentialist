package com.hanielcota.essentials.modules.teleport.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Task;
import org.junit.jupiter.api.Test;

class PendingTeleportTest {

  @Test
  void ownsReturnsTrueForSameCallback() {
    var callback = new TestCallback();
    var pending = new PendingTeleport(Task.noop(), callback);

    assertTrue(pending.owns(callback));
  }

  @Test
  void ownsReturnsFalseForDifferentCallback() {
    var callbackA = new TestCallback();
    var callbackB = new TestCallback();
    var pending = new PendingTeleport(Task.noop(), callbackA);

    assertFalse(pending.owns(callbackB));
  }

  @Test
  void ownsReturnsFalseWhenCallbackIsNull() {
    var callback = new TestCallback();
    var pending = new PendingTeleport(Task.noop(), callback);

    assertFalse(pending.owns(null));
  }

  private static final class TestCallback implements DelayedTeleport.Callback {}
}
