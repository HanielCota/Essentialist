package com.hanielcota.essentials.message;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryMessageProvider implements MutableMessageProvider {

  private final Locale defaultLocale;
  private final Map<Locale, Map<MessageKey, String>> messages = new ConcurrentHashMap<>();

  public InMemoryMessageProvider(Locale defaultLocale) {
    this.defaultLocale = Objects.requireNonNull(defaultLocale, "defaultLocale");
  }

  @Override
  public void put(Locale locale, MessageKey key, String value) {
    Objects.requireNonNull(locale, "locale");
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
    messages.computeIfAbsent(locale, ignored -> new ConcurrentHashMap<>()).put(key, value);
  }

  @Override
  public Locale defaultLocale() {
    return defaultLocale;
  }

  @Override
  public Optional<String> get(Locale locale, MessageKey key) {
    Objects.requireNonNull(locale, "locale");
    Objects.requireNonNull(key, "key");
    var bucket = messages.get(locale);
    if (bucket == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(bucket.get(key));
  }
}
