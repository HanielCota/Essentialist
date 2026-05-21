package com.hanielcota.essentials.message;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public record MessageService(MessageProvider provider) {

  public MessageService(MessageProvider provider) {
    this.provider = Objects.requireNonNull(provider, "provider");
  }

  private static String applyPlaceholders(String template, Map<String, String> values) {
    if (values.isEmpty()) {
      return template;
    }
    String out = template;
    for (Map.Entry<String, String> entry : values.entrySet()) {
      out = out.replace("{" + entry.getKey() + "}", entry.getValue());
    }
    return out;
  }

  public String resolve(MessageKey key) {
    return resolve(provider.defaultLocale(), key, Map.of());
  }

  public String resolve(MessageKey key, Map<String, String> placeholders) {
    return resolve(provider.defaultLocale(), key, placeholders);
  }

  public String resolve(Locale locale, MessageKey key) {
    return resolve(locale, key, Map.of());
  }

  public String resolve(Locale locale, MessageKey key, Map<String, String> placeholders) {
    Objects.requireNonNull(locale, "locale");
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(placeholders, "placeholders");
    String raw =
        provider
            .get(locale, key)
            .or(() -> provider.get(provider.defaultLocale(), key))
            .orElse(key.full());
    return applyPlaceholders(raw, placeholders);
  }
}
