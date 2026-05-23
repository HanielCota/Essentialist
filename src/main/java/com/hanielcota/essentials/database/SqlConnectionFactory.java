package com.hanielcota.essentials.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory for obtaining SQL database connections.
 *
 * <p>By depending on this interface instead of a full database provider, clients only gain access
 * to connection creation, separating execution concerns from pool lifecycle management (ISP).
 */
@FunctionalInterface
public interface SqlConnectionFactory {

  /**
   * Obtains a new database connection.
   *
   * @return a connection to the database
   * @throws SQLException if a database access error occurs
   */
  Connection getConnection() throws SQLException;
}
