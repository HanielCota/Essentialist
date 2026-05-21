package com.hanielcota.essentials.database;

import com.hanielcota.essentials.exception.PluginException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public final class SqliteDatabase implements DatabaseProvider {

  private final Path file;
  private volatile HikariDataSource source;

  public SqliteDatabase(Path file) {
    this.file = Objects.requireNonNull(file, "file");
  }

  @Override
  public synchronized void connect() {
    if (source != null && !source.isClosed()) {
      return;
    }
    ensureParent();

    var config = new HikariConfig();
    config.setPoolName("Essentialist-SQLite");
    config.setDriverClassName("org.sqlite.JDBC");
    config.setJdbcUrl("jdbc:sqlite:" + file);
    config.setMaximumPoolSize(1);
    config.setConnectionTestQuery("SELECT 1");
    config.addDataSourceProperty("foreign_keys", "true");
    config.addDataSourceProperty("journal_mode", "WAL");

    this.source = new HikariDataSource(config);
  }

  @Override
  public synchronized void close() {
    if (source != null) {
      source.close();
      source = null;
    }
  }

  @Override
  public boolean isConnected() {
    var current = source;
    return current != null && !current.isClosed();
  }

  @Override
  public Connection getConnection() throws SQLException {
    var current = source;
    if (current == null || current.isClosed()) {
      throw new SQLException("Database is not connected");
    }
    return current.getConnection();
  }

  private void ensureParent() {
    var parent = file.getParent();
    if (parent == null) {
      return;
    }
    try {
      Files.createDirectories(parent);
    } catch (IOException e) {
      throw new PluginException("Failed to create database directory: " + parent, e);
    }
  }
}
