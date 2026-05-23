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
    try (var conn = connectionFactory.getConnection();
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
    try (var conn = connectionFactory.getConnection();
        var stmt = conn.prepareStatement(sql)) {

      binder.bind(stmt);
      return stmt.executeUpdate();

    } catch (SQLException e) {
      throw new PluginException("SQL update failed: " + sql, e);
    }
  }

  @Override
  public int execute(@NonNull Connection conn, @NonNull String sql, Object... params)
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
    try (var conn = connectionFactory.getConnection()) {
      conn.setAutoCommit(false);
      SQLException primary = null;

      try {
        work.run(conn);
        conn.commit();
      } catch (SQLException e) {
        primary = e;
        try {
          conn.rollback();
        } catch (SQLException rollbackError) {
          primary.addSuppressed(rollbackError);
        }
      } finally {
        try {
          conn.setAutoCommit(true);
        } catch (SQLException restoreError) {
          if (primary == null) {
            primary = restoreError;
          } else if (primary != restoreError) {
            primary.addSuppressed(restoreError);
          }
        }
      }

      if (primary != null) {
        throw primary;
      }
    } catch (SQLException e) {
      throw new PluginException("SQL transaction failed", e);
    }
  }

  @Override
  public void ddl(String... statements) {
    try (var conn = connectionFactory.getConnection();
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
