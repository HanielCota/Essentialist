package com.hanielcota.essentials.event;

import com.hanielcota.essentials.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SimpleEventBus implements EventBus {

  private static final Log LOG = Log.of(SimpleEventBus.class);

  private final Map<Class<?>, List<Registration<?>>> subscribers = new ConcurrentHashMap<>();

  @Override
  public <T extends BasePluginEvent> Subscription subscribe(
      Class<T> type, EventSubscriber<T> subscriber) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(subscriber, "subscriber");

    Registration<T> registration = new Registration<>(subscriber);
    subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(registration);

    return new Subscription() {
      @Override
      public boolean isActive() {
        return registration.active.get();
      }

      @Override
      public void unsubscribe() {
        if (registration.active.compareAndSet(true, false)) {
          List<Registration<?>> list = subscribers.get(type);
          if (list != null) {
            list.remove(registration);
          }
        }
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BasePluginEvent> void publish(T event) {
    Objects.requireNonNull(event, "event");
    List<Registration<?>> list = subscribers.get(event.getClass());
    if (list == null || list.isEmpty()) {
      return;
    }
    for (Registration<?> registration : list) {
      if (!registration.active.get()) {
        continue;
      }
      try {
        ((Registration<T>) registration).subscriber.onEvent(event);
      } catch (RuntimeException e) {
        LOG.error(e, "Subscriber failed for {}", event.getClass().getName());
      }
    }
  }

  private static final class Registration<T extends BasePluginEvent> {
    final EventSubscriber<T> subscriber;
    final AtomicBoolean active = new AtomicBoolean(true);

    Registration(EventSubscriber<T> subscriber) {
      this.subscriber = subscriber;
    }
  }
}
