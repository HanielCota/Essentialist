package com.hanielcota.essentials.event;

@FunctionalInterface
public interface EventSubscriber<T extends BasePluginEvent> {

  void onEvent(T event);
}
