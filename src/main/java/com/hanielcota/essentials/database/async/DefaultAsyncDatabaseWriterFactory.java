package com.hanielcota.essentials.database.async;

import lombok.NonNull;

public final class DefaultAsyncDatabaseWriterFactory implements AsyncDatabaseWriter.Factory {

  @Override
  public AsyncDatabaseWriter create(@NonNull String name) {
    return new DefaultAsyncDatabaseWriter(name);
  }
}
