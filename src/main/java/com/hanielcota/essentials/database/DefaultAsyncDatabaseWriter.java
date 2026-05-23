package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link AsyncDatabaseWriter} that submits tasks to a single-threaded
 * background executor.
 */
public final class DefaultAsyncDatabaseWriter implements AsyncDatabaseWriter {

  private static final Log LOG = Log.of(DefaultAsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;

  private final String threadName;
  private final ExecutorService executor;

  public DefaultAsyncDatabaseWriter(String threadName) {
    this(
        threadName,
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, threadName);
              thread.setDaemon(true);
              return thread;
            }));
  }

  public DefaultAsyncDatabaseWriter(String threadName, ExecutorService executor) {
    this.threadName = threadName;
    this.executor = executor;
  }

  @Override
  public void submit(String operation, Runnable work) {
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
      if (executor.awaitTermination(DRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        return;
      }
      var dropped = executor.shutdownNow().size();
      LOG.warn(
          "{} did not drain in {}s; {} write(s) dropped",
          threadName,
          DRAIN_TIMEOUT_SECONDS,
          dropped);
    } catch (InterruptedException _) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
