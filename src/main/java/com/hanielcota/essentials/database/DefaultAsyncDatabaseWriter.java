package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link AsyncDatabaseWriter} that submits tasks to a single-threaded
 * background executor.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultAsyncDatabaseWriter implements AsyncDatabaseWriter {

  private static final Log LOG = Log.of(DefaultAsyncDatabaseWriter.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;

  private final String threadName;
  private final ExecutorService executor;

  public DefaultAsyncDatabaseWriter(@NonNull String threadName) {
    this(
        threadName,
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, threadName);
              thread.setDaemon(true);
              return thread;
            }));
  }

  public static DefaultAsyncDatabaseWriter of(
      @NonNull String threadName, @NonNull ExecutorService executor) {
    return new DefaultAsyncDatabaseWriter(threadName, executor);
  }

  @Override
  public boolean submit(@NonNull String operation, @NonNull Runnable work) {
    try {
      this.executor.execute(
          () -> {
            try {
              work.run();
            } catch (RuntimeException e) {
              LOG.warn(e, "{} async {} failed", this.threadName, operation);
            }
          });
      return true;
    } catch (RejectedExecutionException _) {
      LOG.warn("{} rejected {} (shutting down?)", this.threadName, operation);
      return false;
    }
  }

  @Override
  public void close() {
    this.executor.shutdown();

    try {
      var timeout = DRAIN_TIMEOUT_SECONDS;
      var unit = TimeUnit.SECONDS;

      if (this.executor.awaitTermination(timeout, unit)) {
        return;
      }

      var droppedTasks = this.executor.shutdownNow();
      var droppedCount = droppedTasks.size();

      LOG.warn(
          "{} did not drain in {}s; {} write(s) dropped", this.threadName, timeout, droppedCount);

    } catch (InterruptedException _) {
      this.executor.shutdownNow();

      var currentThread = Thread.currentThread();
      currentThread.interrupt();
    }
  }
}
