package com.hanielcota.essentials.database;

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
          if (row != null) {
            result.add(row);
          }
        }

        return List.copyOf(result);
      }
    } catch (SQLException e) {
      throw new PluginException("SQL query failed: " + sql, e);
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
      throw new PluginException("SQL update failed: " + sql, e);
    }
  }

  @Override
  public int execute(@NonNull Connection conn, @NonNull String sql, @NonNull Object... params)
      throws SQLException {
    try (var stmt = conn.prepareStatement(sql)) {
      var length = params.length;

      for (var i = 0; i < length; i++) {
        var paramIndex = i + 1;
        var paramValue = params[i];

        stmt.setObject(paramIndex, paramValue);
      }

      return stmt.executeUpdate();
    }
  }

  @Override
  public void tx(@NonNull TxBlock work) {
    try (var conn = this.connectionFactory.getConnection()) {
      conn.setAutoCommit(false);
      SQLException sqlPrimary = null;
      RuntimeException runtimePrimary = null;

      try {
        work.run(conn);
        conn.commit();
      } catch (SQLException e) {
        sqlPrimary = e;
        rollbackQuietly(conn, sqlPrimary);
      } catch (RuntimeException e) {
        // Without this catch, an NPE/IllegalStateException/PluginException raised inside
        // {@code work} would skip the explicit rollback and rely on the connection close to
        // implicitly roll back — driver-dependent. Roll back explicitly so the contract is the
        // same as the SQLException path.
        runtimePrimary = e;
        rollbackQuietly(conn, runtimePrimary);
      } finally {
        try {
          conn.setAutoCommit(true);
        } catch (SQLException restoreError) {
          if (sqlPrimary != null) {
            sqlPrimary.addSuppressed(restoreError);
          } else if (runtimePrimary != null) {
            runtimePrimary.addSuppressed(restoreError);
          } else {
            sqlPrimary = restoreError;
          }
        }
      }

      if (runtimePrimary != null) {
        throw runtimePrimary;
      }
      if (sqlPrimary != null) {
        throw sqlPrimary;
      }
    } catch (SQLException e) {
      throw new PluginException("SQL transaction failed", e);
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
}
