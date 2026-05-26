package com.hanielcota.essentials.database.schema;

import lombok.NonNull;

/**
 * Dialect-specific SQL fragments and statement templates. Centralises the bits of SQL that vary
 * between SQLite, MySQL and Postgres so the rest of the repositories can stay engine-agnostic.
 *
 * <p>Today the only implementation is {@link SqliteDialect}; adding MySQL or Postgres support is a
 * matter of writing a second {@code SqlDialect}, swapping the bootstrap registration, and tuning
 * the connection factory — the per-feature {@code *Table} classes do not need to change.
 *
 * <p>The seam is intentionally narrow: only patterns that actually differ between engines are
 * exposed. Portable SQL ({@code SELECT}, {@code DELETE}, plain {@code UPDATE}, simple {@code CREATE
 * TABLE} with widely-supported types) stays inline in the table installers.
 */
public interface SqlDialect {

  /**
   * Single-statement "insert, or replace the row if its primary key already exists" template.
   * Generated SQL has positional placeholders for every column in declaration order.
   *
   * <ul>
   *   <li>SQLite: {@code INSERT OR REPLACE INTO <table> (<cols>) VALUES (?, ?, ...)}
   *   <li>MySQL: {@code INSERT INTO <table> (<cols>) VALUES (...) ON DUPLICATE KEY UPDATE col =
   *       VALUES(col), ...}
   *   <li>Postgres: {@code INSERT INTO <table> (<cols>) VALUES (...) ON CONFLICT (pk) DO UPDATE SET
   *       col = EXCLUDED.col, ...}
   * </ul>
   *
   * <p>Callers that need to mix literal values with placeholders (e.g. {@code VALUES (1, ?, ?)} for
   * a singleton table) must compose the statement by hand and document the dialect dependency
   * inline.
   */
  String upsertInto(@NonNull String table, @NonNull String... columns);

  /**
   * DDL fragment for a 64-bit auto-incrementing primary key, including the column name. Use inside
   * a {@code CREATE TABLE} body where an {@code id} column is needed.
   *
   * <ul>
   *   <li>SQLite: {@code id INTEGER PRIMARY KEY AUTOINCREMENT}
   *   <li>MySQL: {@code id BIGINT PRIMARY KEY AUTO_INCREMENT}
   *   <li>Postgres: {@code id BIGSERIAL PRIMARY KEY}
   * </ul>
   */
  String autoIncrementPrimaryKey(@NonNull String columnName);

  /**
   * Suffix to append after a {@code TEXT}/{@code VARCHAR} column to make equality comparisons
   * case-insensitive at the storage level. Includes the leading space.
   *
   * <ul>
   *   <li>SQLite: {@code COLLATE NOCASE}
   *   <li>MySQL: {@code COLLATE utf8mb4_unicode_ci}
   *   <li>Postgres: (empty — use {@code CITEXT} column type instead)
   * </ul>
   */
  String caseInsensitiveTextSuffix();

  /**
   * Query that returns a single row iff the supplied {@code (table, column)} pair exists. Bind
   * positional parameters: 1 = table name, 2 = column name. Used by online migrations that need to
   * detect whether an {@code ALTER TABLE ADD COLUMN} has already run.
   */
  String columnExistsQuery();
}
