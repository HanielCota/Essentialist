package com.hanielcota.essentials.database.executor;

/**
 * Composed SQL surface: read + write + transactional + schema-management. Bootstrap registers a
 * single implementation under this type for DI convenience; consumers should prefer the narrow
 * interface they actually need ({@link SqlReader}, {@link SqlWriter}, {@link SqlTransactor}, or
 * {@link SqlSchemaManager}).
 */
public interface SqlExecutor extends SqlReader, SqlWriter, SqlTransactor, SqlSchemaManager {}
