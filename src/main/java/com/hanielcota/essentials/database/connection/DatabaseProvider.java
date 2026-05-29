package com.hanielcota.essentials.database.connection;

/**
 * Owns the connection pool lifecycle on top of the {@link SqlConnectionFactory} contract. {@link
 * #connect()} must be called before any {@link #getConnection()} call, and {@link #close()}
 * releases the pool.
 */
public interface DatabaseProvider extends SqlConnectionFactory, AutoCloseable {

  /**
   * Opens the underlying connection pool. Idempotent — calling it again while already connected is
   * a no-op. Until this has run, {@link #getConnection()} throws.
   */
  void connect();

  /** Closes the connection pool. Idempotent and safe to call when never connected. */
  @Override
  void close();
}
