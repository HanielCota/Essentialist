package com.hanielcota.essentials.database;

import com.hanielcota.essentials.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
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
    this(threadName, newDaemonExecutor(threadName));
  }

  public static DefaultAsyncDatabaseWriter of(
      @NonNull String threadName, @NonNull ExecutorService executor) {
    return new DefaultAsyncDatabaseWriter(threadName, executor);
  }

  private static ExecutorService newDaemonExecutor(@NonNull String threadName) {
    ThreadFactory threadFactory = runnable -> createDaemonThread(runnable, threadName);
    return Executors.newSingleThreadExecutor(threadFactory);
  }

  private static Thread createDaemonThread(@NonNull Runnable runnable, @NonNull String threadName) {
    var thread = new Thread(runnable, threadName);
    thread.setDaemon(true);

    return thread;
  }

  @Override
  public boolean submit(@NonNull String operation, @NonNull Runnable work) {
    Runnable wrapped = () -> runSafely(operation, work);

    try {
      this.executor.execute(wrapped);
      return true;
    } catch (RejectedExecutionException _) {
      LOG.warn("{} rejected {} (shutting down?)", this.threadName, operation);
      return false;
    }
  }

  private void runSafely(@NonNull String operation, @NonNull Runnable work) {
    try {
      work.run();
    } catch (RuntimeException e) {
      LOG.warn(e, "{} async {} failed", this.threadName, operation);
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
