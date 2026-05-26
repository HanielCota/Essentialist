package com.hanielcota.essentials.database.sqlite;

import com.hanielcota.essentials.database.schema.SqlDialect;
import lombok.NonNull;

public final class SqliteDialect implements SqlDialect {

  @Override
  public String upsertInto(@NonNull String table, @NonNull String... columns) {
    var columnList = String.join(", ", columns);

    var placeholders = new StringBuilder(columns.length * 3);
    for (var i = 0; i < columns.length; i++) {
      if (i > 0) {
        placeholders.append(", ");
      }
      placeholders.append('?');
    }

    return "INSERT OR REPLACE INTO "
        + table
        + " ("
        + columnList
        + ") VALUES ("
        + placeholders
        + ")";
  }

  @Override
  public String autoIncrementPrimaryKey(@NonNull String columnName) {
    return columnName + " INTEGER PRIMARY KEY AUTOINCREMENT";
  }

  @Override
  public String caseInsensitiveTextSuffix() {
    return " COLLATE NOCASE";
  }

  @Override
  public String columnExistsQuery() {
    return "SELECT 1 FROM pragma_table_info(?) WHERE name = ?";
  }
}
