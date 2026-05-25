package com.hanielcota.essentials.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;

/**
 * SQLite implementation of {@link DatabaseProvider} that manages connection pool initialization and
 * teardown in a thread-safe manner.
 */
@RequiredArgsConstructor
public final class SqliteDatabase implements DatabaseProvider {

  private final Path file;
  private final AtomicReference<HikariDataSource> sourceRef = new AtomicReference<>();

  @Override
  public void connect() {
    var current = this.sourceRef.get();
    if (current != null && !current.isClosed()) {
      return;
    }

    synchronized (this.sourceRef) {
      current = this.sourceRef.get();
      if (current != null && !current.isClosed()) {
        return;
      }

      silenceHikariLoggers();

      var config = buildHikariConfig();
      var dataSource = new HikariDataSource(config);

      this.sourceRef.set(dataSource);
    }
  }

  private static void silenceHikariLoggers() {
    var essentialsHikariLogger = Logger.getLogger("com.hanielcota.essentials.libs.hikari");
    essentialsHikariLogger.setLevel(Level.WARNING);

    var zaxxerHikariLogger = Logger.getLogger("com.zaxxer.hikari");
    zaxxerHikariLogger.setLevel(Level.WARNING);
  }

  private HikariConfig buildHikariConfig() {
    var jdbcUrl = "jdbc:sqlite:" + this.file;

    var config = new HikariConfig();
    config.setPoolName("Essentialist-SQLite");
    config.setDriverClassName("org.sqlite.JDBC");
    config.setJdbcUrl(jdbcUrl);
    config.setMaximumPoolSize(1);
    config.setConnectionTestQuery("SELECT 1");
    config.addDataSourceProperty("foreign_keys", "true");
    config.addDataSourceProperty("journal_mode", "WAL");

    return config;
  }

  @Override
  public void close() {
    synchronized (this.sourceRef) {
      var current = this.sourceRef.getAndSet(null);
      if (current == null) {
        return;
      }

      current.close();
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    var current = this.sourceRef.get();

    if (current == null || current.isClosed()) {
      throw new SQLException("Database is not connected");
    }

    return current.getConnection();
  }
}
