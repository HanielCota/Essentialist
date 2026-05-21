package com.hanielcota.essentials.config;

public interface ConfigHandle<T> {

  String name();

  T value();

  void reload();
}
