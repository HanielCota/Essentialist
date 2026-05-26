package com.hanielcota.essentials.database.connection;

public interface DatabaseProvider extends SqlConnectionFactory, AutoCloseable {

  void connect();

  @Override
  void close();
}
