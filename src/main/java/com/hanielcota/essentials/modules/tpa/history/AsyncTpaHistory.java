package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import java.util.List;
import java.util.UUID;

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

  private final TpaHistory delegate;
  private final AsyncDatabaseWriter writer;

  public AsyncTpaHistory(TpaHistory delegate) {
    this.delegate = delegate;
    this.writer = new DefaultAsyncDatabaseWriter("Essentialist-TpaHistory");
  }

  @Override
  public void push(TpaHistoryEntry entry) {
    writer.submit("push", () -> delegate.push(entry));
  }

  @Override
  public List<TpaHistoryEntry> list(UUID requester) {
    return delegate.list(requester);
  }

  @Override
  public void close() {
    writer.close();
  }
}
