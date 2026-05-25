package com.hanielcota.essentials.database;

import lombok.NonNull;

/** Schema-management SQL surface: DDL statements (CREATE TABLE / INDEX / etc). */
public interface SqlSchema {

  /** Executes DDL statements. */
  void ddl(@NonNull String... statements);
}
