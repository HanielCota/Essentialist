package com.hanielcota.essentials.database.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.database.schema.SqlDialect;
import org.junit.jupiter.api.Test;

class SqliteDialectTest {

  private final SqlDialect dialect = new SqliteDialect();

  @Test
  void upsertGeneratesInsertOrReplaceWithPlaceholdersForEveryColumn() {
    var sql = this.dialect.upsertInto("homes", "player_id", "name", "world");

    assertEquals("INSERT OR REPLACE INTO homes (player_id, name, world) VALUES (?, ?, ?)", sql);
  }

  @Test
  void upsertWithSingleColumnEmitsOnePlaceholder() {
    var sql = this.dialect.upsertInto("cache", "key");

    assertEquals("INSERT OR REPLACE INTO cache (key) VALUES (?)", sql);
  }

  @Test
  void autoIncrementPrimaryKeyEmitsSqliteAffinityForm() {
    var fragment = this.dialect.autoIncrementPrimaryKey("id");

    assertEquals("id INTEGER PRIMARY KEY AUTOINCREMENT", fragment);
  }

  @Test
  void caseInsensitiveSuffixUsesCollateNocase() {
    assertEquals(" COLLATE NOCASE", this.dialect.caseInsensitiveTextSuffix());
  }

  @Test
  void columnExistsQueryBindsTableAndColumnPositionally() {
    assertEquals(
        "SELECT 1 FROM pragma_table_info(?) WHERE name = ?", this.dialect.columnExistsQuery());
  }
}
