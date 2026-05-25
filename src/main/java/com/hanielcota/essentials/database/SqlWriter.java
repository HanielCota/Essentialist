package com.hanielcota.essentials.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

/** Write-side SQL surface: {@code INSERT}, {@code UPDATE}, {@code DELETE} statements. */
public interface SqlWriter {

  /** Executes an update (INSERT, UPDATE, DELETE) with an explicit binder. */
  void update(@NonNull String sql, @NonNull StatementBinder binder);

  /** Executes an update and returns the number of affected rows. */
  int updateCount(@NonNull String sql, @NonNull StatementBinder binder);

  /** Positional-parameter variant of {@link #update(String, StatementBinder)}. */
  default void update(@NonNull String sql, @NonNull Object... params) {
    updateCount(sql, params);
  }

  /** Positional-parameter variant of {@link #updateCount(String, StatementBinder)}. */
  default int updateCount(@NonNull String sql, @NonNull Object... params) {
    StatementBinder binder = stmt -> bindPositional(stmt, params);

    return updateCount(sql, binder);
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
}
