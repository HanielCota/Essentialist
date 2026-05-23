package com.hanielcota.essentials.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;

/**
 * SQLite implementation of {@link DatabaseProvider} that manages connection pool initialization and
 * teardown in a thread-safe manner.
 */
@RequiredArgsConstructor
public final class SqliteDatabase implements DatabaseProvider {

  private final Path file;
  private final AtomicReference<HikariDataSource> sourceRef = new AtomicReference<>();

  /**
   * Constructs a new SqliteDatabase.
   *
   * @param file the path to the database file
   */
  @Override
  public void connect() {
    var current = sourceRef.get();
    if (current != null && !current.isClosed()) {
      return;
    }

    synchronized (sourceRef) {
      current = sourceRef.get();
      if (current != null && !current.isClosed()) {
        return;
      }

      var config = new HikariConfig();
      config.setPoolName("Essentialist-SQLite");
      config.setDriverClassName("org.sqlite.JDBC");
      config.setJdbcUrl("jdbc:sqlite:" + file);
      config.setMaximumPoolSize(1);
      config.setConnectionTestQuery("SELECT 1");
      config.addDataSourceProperty("foreign_keys", "true");
      config.addDataSourceProperty("journal_mode", "WAL");

      sourceRef.set(new HikariDataSource(config));
    }
  }

  @Override
  public void close() {
    synchronized (sourceRef) {
      var current = sourceRef.getAndSet(null);
      if (current == null) {
        return;
      }
      current.close();
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    var current = sourceRef.get();
    if (current == null) {
      throw new SQLException("Database is not connected");
    }
    if (current.isClosed()) {
      throw new SQLException("Database is not connected");
    }
    return current.getConnection();
  }
}
