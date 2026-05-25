package com.hanielcota.essentials.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    var captured = new AtomicReference<String>();

    callbacks.hop(future, captured::set, "test");

    future.completeExceptionally(new IllegalStateException("boom"));

    // Give the exceptionally chain a moment — but since the consumer is never invoked, captured
    // must remain null. Wait briefly to expose any accidental wiring.
    Thread.sleep(50);
    assertNull(captured.get());
  }

  @Test
  void timeoutCausesTheConsumerToBeSkipped() throws Exception {
    var scheduler = new SynchronousScheduler();
    var callbacks = new MainThreadCallbacks(scheduler);
    var future = new CompletableFuture<String>();
    var captured = new AtomicReference<String>();

    callbacks.hop(future, captured::set, Duration.ofMillis(50), "test");

    Thread.sleep(150);
    assertNull(captured.get());
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
    public void runAsync(@NonNull Runnable task) {
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

    @Override
    public Task runAsyncLater(@NonNull Runnable task, @NonNull Duration delay) {
      return Task.noop();
    }

    @Override
    public Task runAsyncTimer(
        @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
      return Task.noop();
    }
  }
}
