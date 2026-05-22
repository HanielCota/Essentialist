package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Single-thread writer for database mutations that must not block the calling thread.
 *
 * <p>Each task runs sequentially on a daemon thread named {@code threadName}. Failures are logged
 * and never rethrown — fire-and-forget writes (history pushes, audit trails) shouldn't take down a
 * gameplay event because of a transient SQL error.
 *
 * <p>{@link #close()} drains the queue with a fixed timeout. Any tasks still in flight when the
 * timeout elapses are dropped and counted in a warning.
 */
public final class AsyncDatabaseWriter implements AutoCloseable {

  private static final Log LOG = Log.of(AsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;

  private final String threadName;
  private final ExecutorService executor;

  public AsyncDatabaseWriter(String threadName) {
    this.threadName = Objects.requireNonNull(threadName, "threadName");
    this.executor =
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, threadName);
              thread.setDaemon(true);
              return thread;
            });
  }

  /**
   * Schedules {@code work} on the writer thread. If the executor has been shut down the call is a
   * no-op and a warning is logged with {@code operation} as context.
   */
  public void submit(String operation, Runnable work) {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(work, "work");
    try {
      executor.execute(
          () -> {
            try {
              work.run();
            } catch (RuntimeException e) {
              LOG.warn(e, "{} async {} failed", threadName, operation);
            }
          });
    } catch (RejectedExecutionException _) {
      LOG.warn("{} rejected {} (shutting down?)", threadName, operation);
    }
  }

  @Override
  public void close() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(DRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        int dropped = executor.shutdownNow().size();
        LOG.warn(
            "{} did not drain in {}s; {} write(s) dropped",
            threadName,
            DRAIN_TIMEOUT_SECONDS,
            dropped);
      }
    } catch (InterruptedException _) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
