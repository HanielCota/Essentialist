package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link TpaHistory} decorator that moves writes off the calling thread.
 *
 * <p>Sole responsibility: threading. A resolved request is pushed during normal gameplay (accept /
 * deny / disconnect / expiry) and the underlying SQLite INSERT must never block the main thread on
 * disk I/O, so {@link #push} is handed to a single background thread. Reads ({@link #list}) stay
 * synchronous — they only happen on the rare {@code /tpahistory} command. All persistence is
 * delegated to the wrapped {@link TpaHistory}. Writer lifecycle is owned by the module.
 */
@RequiredArgsConstructor
public final class AsyncTpaHistory implements TpaHistory {

  private final TpaHistory delegate;
  private final AsyncDatabaseWriter writer;

  @Override
  public void push(@NonNull TpaHistoryEntry entry) {
    this.writer.submit("push", () -> this.delegate.push(entry));
  }

  @Override
  public List<TpaHistoryEntry> list(@NonNull UUID requester) {
    return this.delegate.list(requester);
  }
}
