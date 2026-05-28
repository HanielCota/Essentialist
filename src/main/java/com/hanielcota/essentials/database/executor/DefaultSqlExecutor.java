package com.hanielcota.essentials.database.executor;

import com.hanielcota.essentials.database.connection.SqlConnectionFactory;
import com.hanielcota.essentials.exception.PluginException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link SqlExecutor} that delegates transactions to {@link
 * TransactionManager} and handles DDL, queries, and updates inline.
 */
@RequiredArgsConstructor
public final class DefaultSqlExecutor implements SqlExecutor {

  private final SqlConnectionFactory connectionFactory;
  private final TransactionManager transactionManager;

  public DefaultSqlExecutor(@NonNull SqlConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    this.transactionManager = new TransactionManager(connectionFactory);
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
  public int execute(
      @NonNull java.sql.Connection conn, @NonNull String sql, @NonNull Object... params)
      throws SQLException {
    try (var stmt = conn.prepareStatement(sql)) {
      var binder = SqlBinders.positional(params);
      binder.bind(stmt);

      return stmt.executeUpdate();
    }
  }

  @Override
  public void tx(@NonNull TxBlock work) {
    this.transactionManager.tx(work);
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
