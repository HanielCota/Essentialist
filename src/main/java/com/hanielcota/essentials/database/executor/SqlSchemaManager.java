package com.hanielcota.essentials.database.executor;

import lombok.NonNull;

/** Schema-management surface: DDL statements ({@code CREATE TABLE}, etc.). */
public interface SqlSchemaManager {

  void ddl(@NonNull String... statements);
}
