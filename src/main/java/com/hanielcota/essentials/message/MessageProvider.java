package com.hanielcota.essentials.message;

import java.util.Locale;
import java.util.Optional;

public interface MessageProvider {

  Locale defaultLocale();

  Optional<String> get(Locale locale, MessageKey key);
}
