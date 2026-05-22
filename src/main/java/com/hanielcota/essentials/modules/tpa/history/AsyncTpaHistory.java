package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.util.Log;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * {@link TpaHistory} decorator that moves writes off the calling thread.
 *
 * <p>Sole responsibility: threading. A resolved request is pushed during normal gameplay (accept /
 * deny / disconnect / expiry) and the underlying SQLite INSERT must never block the main thread on
 * disk I/O, so {@link #push} is handed to a single background thread. Reads ({@link #list}) stay
 * synchronous — they only happen on the rare {@code /tpahistory} command. All persistence is
 * delegated to the wrapped {@link TpaHistory}.
 */
public final class AsyncTpaHistory implements TpaHistory, AutoCloseable {

  private static final Log LOG = Log.of(AsyncTpaHistory.class);
  private static final int DRAIN_TIMEOUT_SECONDS = 5;

  private final TpaHistory delegate;
  private final ExecutorService writeExecutor;

  public AsyncTpaHistory(TpaHistory delegate) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.writeExecutor =
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, "Essentialist-TpaHistory");
              thread.setDaemon(true);
              return thread;
            });
  }

  @Override
  public void push(TpaHistoryEntry entry) {
    Objects.requireNonNull(entry, "entry");
    try {
      writeExecutor.execute(() -> pushSafely(entry));
    } catch (RejectedExecutionException _) {
      LOG.warn("Tpa-history executor rejected a write (plugin shutting down?)");
    }
  }

  @Override
  public List<TpaHistoryEntry> list(UUID requester) {
    return delegate.list(requester);
  }

  private void pushSafely(TpaHistoryEntry entry) {
    try {
      delegate.push(entry);
    } catch (RuntimeException e) {
      LOG.warn(e, "Async tpa-history write failed");
    }
  }

  @Override
  public void close() {
    writeExecutor.shutdown();
    try {
      if (!writeExecutor.awaitTermination(DRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        int dropped = writeExecutor.shutdownNow().size();
        LOG.warn(
            "Tpa-history writer did not drain in {}s; {} write(s) dropped",
            DRAIN_TIMEOUT_SECONDS,
            dropped);
      }
    } catch (InterruptedException _) {
      writeExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
