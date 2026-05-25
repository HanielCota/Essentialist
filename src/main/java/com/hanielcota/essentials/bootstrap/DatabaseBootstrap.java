package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.DefaultSqlExecutor;
import com.hanielcota.essentials.database.SqlConnectionFactory;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.database.SqliteDatabase;
import com.hanielcota.essentials.exception.PluginException;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.io.IOException;
import java.nio.file.Files;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DatabaseBootstrap {

  private final EssentialsPlugin plugin;

  void register(@NonNull ServiceRegistry services) {
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

    services.register(DatabaseProvider.class, database);
    services.register(SqlConnectionFactory.class, database);

    var sqlExecutor = new DefaultSqlExecutor(database);
    services.register(SqlExecutor.class, sqlExecutor);
  }
}
