package com.hanielcota.essentials.event;

public interface EventBus {

  <T extends BasePluginEvent> Subscription subscribe(Class<T> type, EventSubscriber<T> subscriber);

  <T extends BasePluginEvent> void publish(T event);
}
