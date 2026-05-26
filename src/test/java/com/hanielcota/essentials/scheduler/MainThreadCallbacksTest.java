package com.hanielcota.essentials.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;

class MainThreadCallbacksTest {

  @Test
  void valueIsHandedToConsumerWhenFutureCompletes() throws Exception {
    var scheduler = new SynchronousScheduler();
    var callbacks = new MainThreadCallbacks(scheduler);
    var future = new CompletableFuture<String>();
    var captured = new AtomicReference<String>();
    var done = new CountDownLatch(1);

    callbacks.hop(
        future,
        value -> {
          captured.set(value);
          done.countDown();
        },
        "test");

    future.complete("ok");
    assertTrue(done.await(2, TimeUnit.SECONDS));
    assertEquals("ok", captured.get());
  }

  @Test
  void consumerIsNotCalledWhenFutureFails() throws Exception {
    var scheduler = new SynchronousScheduler();
    var callbacks = new MainThreadCallbacks(scheduler);
    var future = new CompletableFuture<String>();
    var consumerInvoked = new CountDownLatch(1);

    callbacks.hop(future, value -> consumerInvoked.countDown(), "test");

    future.completeExceptionally(new IllegalStateException("boom"));

    // Latch never released = consumer never ran. The await is expected to time out (return
    // false), not to race the consumer.
    var released = consumerInvoked.await(100, TimeUnit.MILLISECONDS);
    assertFalse(released, "consumer must not be invoked when the future fails");
  }

  @Test
  void timeoutCausesTheConsumerToBeSkipped() throws Exception {
    var scheduler = new SynchronousScheduler();
    var callbacks = new MainThreadCallbacks(scheduler);
    var future = new CompletableFuture<String>();
    var consumerInvoked = new CountDownLatch(1);

    callbacks.hop(future, value -> consumerInvoked.countDown(), Duration.ofMillis(50), "test");

    // The configured 50ms orTimeout fires → exceptionally branch runs → consumer never invoked.
    // Awaiting longer than the configured timeout (with a grace window) lets us assert the latch
    // never releases without relying on Thread.sleep timing.
    var released = consumerInvoked.await(250, TimeUnit.MILLISECONDS);
    assertFalse(released, "consumer must not be invoked when the future times out");
  }

  private static final class SynchronousScheduler implements Scheduler {

    @Override
    public Executor mainExecutor() {
      return Runnable::run;
    }

    @Override
    public void runSync(@NonNull Runnable task) {
      task.run();
    }

    @Override
    public void runOnEntity(@NonNull Entity entity, @NonNull Runnable task) {
      task.run();
    }

    @Override
    public Task runOnEntityLater(
        @NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay) {
      return Task.noop();
    }

    @Override
    public Task runLater(@NonNull Runnable task, @NonNull Duration delay) {
      return Task.noop();
    }

    @Override
    public Task runTimer(
        @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
      return Task.noop();
    }
  }
}
