package com.hanielcota.essentials.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.NonNull;

/** Read-only SQL surface: {@code SELECT}-style queries that return mapped rows. */
public interface SqlReader {

  private static void bindPositional(@NonNull PreparedStatement stmt, @NonNull Object[] params)
      throws SQLException {
    var length = params.length;

    for (var i = 0; i < length; i++) {
      var paramIndex = i + 1;
      var paramValue = params[i];

      stmt.setObject(paramIndex, paramValue);
    }
  }

  /** Executes a query with an explicit binder and maps each row of the result set. */
  <T> List<T> query(
      @NonNull String sql, @NonNull StatementBinder binder, @NonNull ResultMapper<T> mapper);

  /** Positional-parameter variant of {@link #query(String, StatementBinder, ResultMapper)}. */
  default <T> List<T> query(
      @NonNull String sql, @NonNull ResultMapper<T> mapper, @NonNull Object... params) {
    StatementBinder binder = stmt -> bindPositional(stmt, params);

    return query(sql, binder, mapper);
  }
}
