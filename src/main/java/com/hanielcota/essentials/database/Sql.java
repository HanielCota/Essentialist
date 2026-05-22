package com.hanielcota.essentials.database;

import com.hanielcota.essentials.exception.PluginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Sql {

  private Sql() {}

  public static <T> List<T> query(
      DatabaseProvider db, String sql, StatementBinder binder, ResultMapper<T> mapper) {
    try (var conn = db.getConnection();
        var stmt = conn.prepareStatement(sql)) {
      binder.bind(stmt);
      try (var rs = stmt.executeQuery()) {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
          T row = mapper.map(rs);
          if (row != null) {
            result.add(row);
          }
        }
        return result;
      }
    } catch (SQLException e) {
      throw new PluginException("SQL query failed: " + sql, e);
    }
  }

  /**
   * Positional-parameter variant of {@link #query(DatabaseProvider, String, StatementBinder,
   * ResultMapper)} — each {@code param} is bound via {@code setObject(i+1, value)}, which the JDBC
   * driver maps for standard types (String, Integer, Long, Double, Boolean, byte[], etc.). Use the
   * binder overload when the values need custom typing or null handling.
   */
  public static <T> List<T> query(
      DatabaseProvider db, String sql, ResultMapper<T> mapper, Object... params) {
    return query(db, sql, stmt -> bindAll(stmt, params), mapper);
  }

  public static int update(DatabaseProvider db, String sql, StatementBinder binder) {
    try (var conn = db.getConnection();
        var stmt = conn.prepareStatement(sql)) {
      binder.bind(stmt);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new PluginException("SQL update failed: " + sql, e);
    }
  }

  /**
   * Positional-parameter variant of {@link #update(DatabaseProvider, String, StatementBinder)}; see
   * {@link #query(DatabaseProvider, String, ResultMapper, Object...)} for the binding semantics.
   */
  public static int update(DatabaseProvider db, String sql, Object... params) {
    return update(db, sql, stmt -> bindAll(stmt, params));
  }

  /**
   * Executes one statement on an existing connection — typically inside a {@link
   * #tx(DatabaseProvider, TxBlock)} lambda so several writes share the same transaction. Binds via
   * the same {@code setObject} rules as the varargs overloads.
   */
  public static int execute(Connection conn, String sql, Object... params) throws SQLException {
    try (var stmt = conn.prepareStatement(sql)) {
      bindAll(stmt, params);
      return stmt.executeUpdate();
    }
  }

  private static void bindAll(PreparedStatement stmt, Object[] params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      stmt.setObject(i + 1, params[i]);
    }
  }

  public static void tx(DatabaseProvider db, TxBlock work) {
    try (var conn = db.getConnection()) {
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
          if (primary != null) {
            primary.addSuppressed(restoreError);
          } else {
            primary = restoreError;
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

  public static void ddl(DatabaseProvider db, String... statements) {
    try (var conn = db.getConnection();
        var stmt = conn.createStatement()) {
      for (String s : statements) {
        stmt.execute(s);
      }
    } catch (SQLException e) {
      throw new PluginException("SQL DDL failed", e);
    }
  }

  @FunctionalInterface
  public interface StatementBinder {
    void bind(PreparedStatement stmt) throws SQLException;
  }

  @FunctionalInterface
  public interface ResultMapper<T> {
    T map(ResultSet rs) throws SQLException;
  }

  @FunctionalInterface
  public interface TxBlock {
    void run(Connection conn) throws SQLException;
  }
}
