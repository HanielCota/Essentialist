package com.hanielcota.essentials.database;

/**
 * Composed SQL surface: read + write + transactional + schema-management. Bootstrap registers a
 * single implementation under this type so any consumer can take the narrow interface it actually
 * needs ({@link SqlReader}, {@link SqlWriter}, {@link SqlTransactor}, {@link SqlSchema}).
 */
public interface SqlExecutor extends SqlReader, SqlWriter, SqlTransactor, SqlSchema {}
