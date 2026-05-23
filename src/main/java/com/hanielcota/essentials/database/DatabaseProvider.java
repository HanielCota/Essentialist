package com.hanielcota.essentials.database;

public interface DatabaseProvider extends SqlConnectionFactory, AutoCloseable {

  void connect();

  @Override
  void close();
}
