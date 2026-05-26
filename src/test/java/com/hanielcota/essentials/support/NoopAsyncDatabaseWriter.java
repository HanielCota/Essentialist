package com.hanielcota.essentials.support;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;

public enum NoopAsyncDatabaseWriter implements AsyncDatabaseWriter {
  INSTANCE;

  @Override
  public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void close() {}
}
