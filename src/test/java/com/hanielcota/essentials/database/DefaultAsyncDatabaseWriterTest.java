package com.hanielcota.essentials.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class DefaultAsyncDatabaseWriterTest {

  @Test
  void submittedWorkRunsAndFutureCompletesWithoutValue() throws Exception {
    try (var writer = new DefaultAsyncDatabaseWriter("test-writer")) {
      var counter = new AtomicInteger();

      var future = writer.submit("inc", counter::incrementAndGet);
      future.get(2, TimeUnit.SECONDS);

      assertEquals(1, counter.get());
    }
  }

  @Test
  void workExceptionPropagatesThroughTheFuture() {
    try (var writer = new DefaultAsyncDatabaseWriter("test-writer")) {
      Runnable fail =
          () -> {
            throw new IllegalStateException("boom");
          };

      var future = writer.submit("fail", fail);

      var executionException = assertThrows(ExecutionException.class, future::get);
      var cause = executionException.getCause();
      assertInstanceOf(IllegalStateException.class, cause);
      assertEquals("boom", cause.getMessage());
    }
  }

  @Test
  void overflowingTheQueueRejectsTheSubmissionViaFutureException() throws Exception {
    // capacity=1 plus one in-flight work means the third submit hits a full queue.
    try (var writer = new DefaultAsyncDatabaseWriter("test-writer", 1)) {
      var blocker = new CountDownLatch(1);
      var firstStarted = new CountDownLatch(1);

      Runnable block =
          () -> {
            firstStarted.countDown();
            try {
              blocker.await();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          };

      writer.submit("block", block);
      assertTrue(firstStarted.await(2, TimeUnit.SECONDS), "first task should start");

      // Worker is parked → the next submit fills the 1-slot queue.
      writer.submit("fill", () -> {});

      // This third submit must be rejected — both the worker and the queue are full.
      CompletableFuture<Void> rejected = writer.submit("overflow", () -> {});
      blocker.countDown();

      var completion = assertThrows(CompletionException.class, rejected::join);
      var cause = completion.getCause();
      assertNotNull(cause);
      assertInstanceOf(RejectedExecutionException.class, cause);
    }
  }
}
