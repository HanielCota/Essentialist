package com.hanielcota.essentials.database.schema;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import lombok.NonNull;

/**
 * Skeleton for table definitions that share the same constructor shape ({@code dialect.upsertInto})
 * and {@link #install} method. Subclasses only need to supply SQL constants and the DDL string.
 */
public abstract class SqlTable {

  private final String upsert;
  private final String createTable;

  protected SqlTable(
      @NonNull SqlDialect dialect,
      @NonNull String tableName,
      @NonNull String ddl,
      @NonNull String... columns) {
    this.upsert = dialect.upsertInto(tableName, columns);
    this.createTable = ddl;
  }

  public String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(this.createTable);
  }
}
