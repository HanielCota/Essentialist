package com.hanielcota.essentials.database.executor;

import com.hanielcota.essentials.database.connection.SqlConnectionFactory;
import com.hanielcota.essentials.exception.PluginException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link SqlExecutor} that handles resource cleanup and transaction
 * boundaries using standard JDBC operations.
 */
@RequiredArgsConstructor
public final class DefaultSqlExecutor implements SqlExecutor {

  private final SqlConnectionFactory connectionFactory;

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

  @Override
  public <T> List<T> query(
      @NonNull String sql, @NonNull StatementBinder binder, @NonNull ResultMapper<T> mapper) {
    try (var conn = this.connectionFactory.getConnection();
        var stmt = conn.prepareStatement(sql)) {

      binder.bind(stmt);

      try (var rs = stmt.executeQuery()) {
        var result = new ArrayList<T>();

        while (rs.next()) {
          var row = mapper.map(rs);
          if (row == null) {
            continue;
          }
          result.add(row);
        }

        return List.copyOf(result);
      }
    } catch (SQLException e) {
      var failureMessage = "SQL query failed: " + sql;
      throw new PluginException(failureMessage, e);
    }
  }

  @Override
  public void update(@NonNull String sql, @NonNull StatementBinder binder) {
    updateCount(sql, binder);
  }

  @Override
  public int updateCount(@NonNull String sql, @NonNull StatementBinder binder) {
    try (var conn = this.connectionFactory.getConnection();
        var stmt = conn.prepareStatement(sql)) {

      binder.bind(stmt);
      return stmt.executeUpdate();

    } catch (SQLException e) {
      var failureMessage = "SQL update failed: " + sql;
      throw new PluginException(failureMessage, e);
    }
  }

  @Override
  public int execute(@NonNull Connection conn, @NonNull String sql, @NonNull Object... params)
      throws SQLException {
    try (var stmt = conn.prepareStatement(sql)) {
      var binder = SqlBinders.positional(params);
      binder.bind(stmt);

      return stmt.executeUpdate();
    }
  }

  @Override
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
        // Without this catch, an NPE/IllegalStateException/PluginException raised inside
        // {@code work} would skip the explicit rollback and rely on the connection close to
        // implicitly roll back — driver-dependent. Roll back explicitly so the contract is the
        // same as the SQLException path.
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

  @Override
  public void ddl(@NonNull String... statements) {
    try (var conn = this.connectionFactory.getConnection();
        var stmt = conn.createStatement()) {

      for (var statement : statements) {
        if (statement == null || statement.isBlank()) {
          continue;
        }
        stmt.addBatch(statement);
      }

      stmt.executeBatch();

    } catch (SQLException e) {
      throw new PluginException("SQL DDL failed", e);
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
