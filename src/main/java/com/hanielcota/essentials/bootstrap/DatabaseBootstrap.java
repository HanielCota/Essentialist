package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriterFactory;
import com.hanielcota.essentials.database.DefaultSqlExecutor;
import com.hanielcota.essentials.database.SqlConnectionFactory;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.database.SqliteDatabase;
import com.hanielcota.essentials.database.SqliteDialect;
import com.hanielcota.essentials.exception.PluginException;
import java.io.IOException;
import java.nio.file.Files;
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
    var dataFolder = this.plugin.getDataFolder();
    var dataFolderPath = dataFolder.toPath();
    var dataDir = dataFolderPath.resolve("data");
    var dbPath = dataDir.resolve("essentials.db");
    var parentDir = dbPath.getParent();

    try {
      Files.createDirectories(parentDir);
    } catch (IOException e) {
      var errorMessage = "Failed to create database directory: " + parentDir;
      throw new PluginException(errorMessage, e);
    }

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
