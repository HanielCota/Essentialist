package com.hanielcota.essentials.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseProvider {

  void connect();

  void close();

  boolean isConnected();

  Connection getConnection() throws SQLException;
}
