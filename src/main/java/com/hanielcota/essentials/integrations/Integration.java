package com.hanielcota.essentials.integrations;

public interface Integration {

  String name();

  boolean isAvailable();

  void enable();

  void disable();
}
