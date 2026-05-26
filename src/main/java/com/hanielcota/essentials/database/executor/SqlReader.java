package com.hanielcota.essentials.database.executor;

import java.util.List;
import lombok.NonNull;

/** Read-only SQL surface: {@code SELECT}-style queries that return mapped rows. */
public interface SqlReader {

  /** Executes a query with an explicit binder and maps each row of the result set. */
  <T> List<T> query(
      @NonNull String sql, @NonNull StatementBinder binder, @NonNull ResultMapper<T> mapper);

  /** Positional-parameter variant of {@link #query(String, StatementBinder, ResultMapper)}. */
  default <T> List<T> query(
      @NonNull String sql, @NonNull ResultMapper<T> mapper, @NonNull Object... params) {
    var binder = SqlBinders.positional(params);

    return query(sql, binder, mapper);
  }
}
