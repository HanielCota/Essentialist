package com.hanielcota.essentials.event;

public interface Subscription {

  boolean isActive();

  void unsubscribe();
}
