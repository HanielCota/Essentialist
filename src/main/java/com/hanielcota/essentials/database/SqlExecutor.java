package com.hanielcota.essentials.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
  <T> List<T> query(String sql, StatementBinder binder, ResultMapper<T> mapper);

  /**
   * Positional-parameter variant of query.
   *
   * @param sql the query SQL
   * @param mapper row mapper
   * @param params query parameters
   * @param <T> the row type
   * @return a list of mapped results, excluding null values
   */
  default <T> List<T> query(String sql, ResultMapper<T> mapper, Object... params) {
    return query(
        sql,
        stmt -> {
          for (var i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
          }
        },
        mapper);
  }

  /**
   * Executes an update (INSERT, UPDATE, DELETE).
   *
   * @param sql the update SQL
   * @param binder statement parameter binder
   */
  void update(String sql, StatementBinder binder);

  /**
   * Executes an update and returns the number of affected rows.
   *
   * @param sql the update SQL
   * @param binder statement parameter binder
   * @return affected row count
   */
  int updateCount(String sql, StatementBinder binder);

  /**
   * Positional-parameter variant of update.
   *
   * @param sql the update SQL
   * @param params update parameters
   */
  default void update(String sql, Object... params) {
    updateCount(sql, params);
  }

  /**
   * Positional-parameter variant of updateCount.
   *
   * @param sql the update SQL
   * @param params update parameters
   * @return affected row count
   */
  default int updateCount(String sql, Object... params) {
    return updateCount(
        sql,
        stmt -> {
          for (var i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
          }
        });
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
  int execute(Connection conn, String sql, Object... params) throws SQLException;

  /**
   * Runs a block of operations within a transaction.
   *
   * @param work the transactional block to run
   */
  void tx(TxBlock work);

  /**
   * Executes DDL statements.
   *
   * @param statements the DDL statements to run
   */
  void ddl(String... statements);

  /** Functional interface for binding parameters to a PreparedStatement. */
  @FunctionalInterface
  interface StatementBinder {
    void bind(PreparedStatement stmt) throws SQLException;
  }

  /**
   * Functional interface for mapping a ResultSet row to a Java object.
   *
   * @param <T> the target type
   */
  @FunctionalInterface
  interface ResultMapper<T> {
    T map(ResultSet rs) throws SQLException;
  }

  /** Functional interface representing a block of work to be run in a database transaction. */
  @FunctionalInterface
  interface TxBlock {
    void run(Connection conn) throws SQLException;
  }
}
