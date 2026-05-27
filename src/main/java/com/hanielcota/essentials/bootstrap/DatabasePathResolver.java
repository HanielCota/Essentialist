package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.exception.PluginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;

/**
 * Resolves the database file path and ensures the parent directory exists on disk. Extracted from
 * {@link DatabaseBootstrap} so filesystem I/O is separate from service construction and
 * registration.
 */
final class DatabasePathResolver {

  private DatabasePathResolver() {}

  static Path resolve(@NonNull EssentialsPlugin plugin) {
    var dataFolder = plugin.getDataFolder();
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

    return dbPath;
  }
}
