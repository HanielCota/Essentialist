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

  public static int update(DatabaseProvider db, String sql, StatementBinder binder) {
    try (var conn = db.getConnection();
        var stmt = conn.prepareStatement(sql)) {
      binder.bind(stmt);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new PluginException("SQL update failed: " + sql, e);
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
