package com.hanielcota.essentials.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.NonNull;

/**
 * Interface defining common SQL execution patterns.
 *
 * <p>By relying on this interface instead of static methods, database logic becomes testable,
 * extensible (via decorators/proxies), and adheres to the Dependency Inversion Principle (DIP).
 */
public interface SqlExecutor {

  /**
   * Executes a query and maps each row of the result set.
   *
   * @param sql the query SQL
   * @param binder statement parameter binder
   * @param mapper row mapper
   * @param <T> the row type
   * @return a list of mapped results, excluding null values
   */
  <T> List<T> query(
      @NonNull String sql, @NonNull StatementBinder binder, @NonNull ResultMapper<T> mapper);

  /**
   * Positional-parameter variant of query.
   *
   * @param sql the query SQL
   * @param mapper row mapper
   * @param params query parameters
   * @param <T> the row type
   * @return a list of mapped results, excluding null values
   */
  default <T> List<T> query(
      @NonNull String sql, @NonNull ResultMapper<T> mapper, @NonNull Object... params) {
    StatementBinder binder = stmt -> bindPositional(stmt, params);

    return query(sql, binder, mapper);
  }

  private static void bindPositional(@NonNull PreparedStatement stmt, @NonNull Object[] params)
      throws SQLException {
    var length = params.length;

    for (var i = 0; i < length; i++) {
      var paramIndex = i + 1;
      var paramValue = params[i];

      stmt.setObject(paramIndex, paramValue);
    }
  }

  /**
   * Executes an update (INSERT, UPDATE, DELETE).
   *
   * @param sql the update SQL
   * @param binder statement parameter binder
   */
  void update(@NonNull String sql, @NonNull StatementBinder binder);

  /**
   * Executes an update and returns the number of affected rows.
   *
   * @param sql the update SQL
   * @param binder statement parameter binder
   * @return affected row count
   */
  int updateCount(@NonNull String sql, @NonNull StatementBinder binder);

  /**
   * Positional-parameter variant of update.
   *
   * @param sql the update SQL
   * @param params update parameters
   */
  default void update(@NonNull String sql, @NonNull Object... params) {
    updateCount(sql, params);
  }

  /**
   * Positional-parameter variant of updateCount.
   *
   * @param sql the update SQL
   * @param params update parameters
   * @return affected row count
   */
  default int updateCount(@NonNull String sql, @NonNull Object... params) {
    StatementBinder binder = stmt -> bindPositional(stmt, params);

    return updateCount(sql, binder);
  }

  /**
   * Executes a single update statement on an existing connection, typically inside a transaction
   * block.
   *
   * @param conn the active database connection
   * @param sql the statement SQL
   * @param params parameters to bind
   * @return number of affected rows
   * @throws SQLException if a database access error occurs
   */
  int execute(@NonNull Connection conn, @NonNull String sql, @NonNull Object... params)
      throws SQLException;

  /**
   * Runs a block of operations within a transaction.
   *
   * @param work the transactional block to run
   */
  void tx(@NonNull TxBlock work);

  /**
   * Executes DDL statements.
   *
   * @param statements the DDL statements to run
   */
  void ddl(@NonNull String... statements);

  /** Functional interface for binding parameters to a PreparedStatement. */
  @FunctionalInterface
  interface StatementBinder {
    void bind(@NonNull PreparedStatement stmt) throws SQLException;
  }

  /**
   * Functional interface for mapping a ResultSet row to a Java object.
   *
   * @param <T> the target type
   */
  @FunctionalInterface
  interface ResultMapper<T> {
    T map(@NonNull ResultSet rs) throws SQLException;
  }

  /** Functional interface representing a block of work to be run in a database transaction. */
  @FunctionalInterface
  interface TxBlock {
    void run(@NonNull Connection conn) throws SQLException;
  }
}
