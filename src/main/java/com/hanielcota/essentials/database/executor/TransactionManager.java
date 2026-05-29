package com.hanielcota.essentials.database.executor;

import com.hanielcota.essentials.database.connection.SqlConnectionFactory;
import com.hanielcota.essentials.exception.PluginException;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Manages JDBC transaction boundaries: commit, rollback, auto-commit restoration, and error
 * aggregation. Separated from {@link DefaultSqlExecutor} so the executor stays focused on statement
 * execution.
 */
@RequiredArgsConstructor
public final class TransactionManager {

  private final SqlConnectionFactory connectionFactory;

  /**
   * Runs {@code work} inside a single transaction: auto-commit is disabled for the duration,
   * committed on success, and rolled back if {@code work} throws. Auto-commit is always restored
   * and the connection closed before returning.
   *
   * <p>If {@code work} throws, the original exception propagates with the rollback (and any
   * auto-commit-restore) failure attached as a suppressed exception; a runtime exception takes
   * precedence over a {@link SQLException} when both are in play. Any {@code SQLException} escaping
   * the connection/transaction machinery itself is wrapped in a {@link PluginException}.
   *
   * @throws PluginException if obtaining the connection or driving the transaction fails
   */
  public void tx(@NonNull TxBlock work) {
    try (var conn = this.connectionFactory.getConnection()) {
      conn.setAutoCommit(false);
      var state = new TxState();

      try {
        work.run(conn);
        conn.commit();
      } catch (SQLException e) {
        state.sqlPrimary = e;
        rollbackQuietly(conn, e);
      } catch (RuntimeException e) {
        state.runtimePrimary = e;
        rollbackQuietly(conn, e);
      } finally {
        restoreAutoCommit(conn, state);
      }

      rethrowPrimary(state);
    } catch (SQLException e) {
      throw new PluginException("SQL transaction failed", e);
    }
  }

  private static void restoreAutoCommit(@NonNull Connection conn, @NonNull TxState state) {
    try {
      conn.setAutoCommit(true);
    } catch (SQLException restoreError) {
      state.attachSuppressedOrAdopt(restoreError);
    }
  }

  private static void rethrowPrimary(@NonNull TxState state) throws SQLException {
    if (state.runtimePrimary != null) {
      throw state.runtimePrimary;
    }
    if (state.sqlPrimary != null) {
      throw state.sqlPrimary;
    }
  }

  private static void rollbackQuietly(@NonNull Connection conn, @NonNull Throwable primary) {
    try {
      conn.rollback();
    } catch (SQLException rollbackError) {
      primary.addSuppressed(rollbackError);
    }
  }

  private static final class TxState {
    SQLException sqlPrimary;
    RuntimeException runtimePrimary;

    void attachSuppressedOrAdopt(@NonNull SQLException restoreError) {
      if (this.sqlPrimary != null) {
        this.sqlPrimary.addSuppressed(restoreError);
        return;
      }
      if (this.runtimePrimary != null) {
        this.runtimePrimary.addSuppressed(restoreError);
        return;
      }
      this.sqlPrimary = restoreError;
    }
  }
}
