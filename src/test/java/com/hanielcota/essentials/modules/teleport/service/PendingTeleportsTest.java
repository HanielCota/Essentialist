package com.hanielcota.essentials.modules.teleport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hanielcota.essentials.scheduler.Task;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PendingTeleportsTest {

  @Test
  void putAndRemove() {
    var pending = new PendingTeleports();
    var id = UUID.randomUUID();
    var callback = new RecordingCallback();

    pending.put(id, Task.noop(), callback);

    var removed = pending.remove(id);
    assertNotNull(removed);
    assertEquals(callback, removed.callback());
  }

  @Test
  void removeReturnsNullForUnknownPlayer() {
    var pending = new PendingTeleports();

    assertNull(pending.remove(UUID.randomUUID()));
  }

  @Test
  void cancelSilentlyRemovesAndCancelsTask() {
    var pending = new PendingTeleports();
    var id = UUID.randomUUID();
    var cancelled = new boolean[] {false};

    var task =
        new Task() {
          @Override
          public void cancel() {
            cancelled[0] = true;
          }

          @Override
          public boolean isCancelled() {
            return cancelled[0];
          }
        };
    pending.put(id, task, new RecordingCallback());
    pending.cancelSilently(id);

    assertNull(pending.remove(id));
    assert cancelled[0];
  }

  @Test
  void cancelSilentlyDoesNothingForUnknownPlayer() {
    var pending = new PendingTeleports();

    pending.cancelSilently(UUID.randomUUID());
  }

  private static final class RecordingCallback implements DelayedTeleport.Callback {}
}
