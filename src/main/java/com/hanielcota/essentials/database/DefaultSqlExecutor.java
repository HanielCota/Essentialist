package com.hanielcota.essentials.database;

import com.hanielcota.essentials.exception.PluginException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link SqlExecutor} that handles resource cleanup and transaction
 * boundaries using standard JDBC operations.
 */
@RequiredArgsConstructor
public final class DefaultSqlExecutor implements SqlExecutor {

  private final SqlConnectionFactory connectionFactory;

  /**
   * Constructs a new DefaultSqlExecutor.
   *
   * @param connectionFactory the connection factory to use
   */
  @Override
  public <T> List<T> query(String sql, StatementBinder binder, ResultMapper<T> mapper) {

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
  public void update(String sql, StatementBinder binder) {

    try (var conn = connectionFactory.getConnection();
        var stmt = conn.prepareStatement(sql)) {

      binder.bind(stmt);
      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new PluginException("SQL update failed: " + sql, e);
    }
  }

  @Override
  public int execute(Connection conn, String sql, Object... params) throws SQLException {

    try (var stmt = conn.prepareStatement(sql)) {
      for (var i = 0; i < params.length; i++) {
        stmt.setObject(i + 1, params[i]);
      }
      return stmt.executeUpdate();
    }
  }

  @Override
  public void tx(TxBlock work) {

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
          } else {
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

      for (var s : statements) {
        if (s != null && !s.isBlank()) {
          stmt.addBatch(s);
        }
      }
      stmt.executeBatch();

    } catch (SQLException e) {
      throw new PluginException("SQL DDL failed", e);
    }
  }
}
