package com.hanielcota.essentials.database.executor;

import lombok.NonNull;

/**
 * Composed SQL surface: read + write + transactional + schema-management. Bootstrap registers a
 * single implementation under this type so any consumer can take the narrow interface it actually
 * needs ({@link SqlReader}, {@link SqlWriter}, {@link SqlTransactor}).
 *
 * <p>{@code ddl} lives here directly because there is no consumer that benefits from a narrower
 * "schema-only" view — DDL is always called from per-module table installers.
 */
public interface SqlExecutor extends SqlReader, SqlWriter, SqlTransactor {

  /** Executes DDL statements. */
  void ddl(@NonNull String... statements);
}
