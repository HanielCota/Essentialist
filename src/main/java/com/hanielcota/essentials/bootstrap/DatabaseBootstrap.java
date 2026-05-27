package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.async.DefaultAsyncDatabaseWriterFactory;
import com.hanielcota.essentials.database.connection.DatabaseProvider;
import com.hanielcota.essentials.database.connection.SqlConnectionFactory;
import com.hanielcota.essentials.database.executor.DefaultSqlExecutor;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.sqlite.SqliteDatabase;
import com.hanielcota.essentials.database.sqlite.SqliteDialect;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DatabaseBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "database";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var dbPath = DatabasePathResolver.resolve(this.plugin);

    var database = new SqliteDatabase(dbPath);
    database.connect();

    var services = context.services();
    services.register(DatabaseProvider.class, database);
    services.register(SqlConnectionFactory.class, database);

    var sqlExecutor = new DefaultSqlExecutor(database);
    services.register(SqlExecutor.class, sqlExecutor);

    services.register(SqlDialect.class, new SqliteDialect());

    services.register(AsyncDatabaseWriter.Factory.class, new DefaultAsyncDatabaseWriterFactory());
  }
}
