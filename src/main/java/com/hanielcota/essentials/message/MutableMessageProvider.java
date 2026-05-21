package com.hanielcota.essentials.message;

import java.util.Locale;

public interface MutableMessageProvider extends MessageProvider {

  void put(Locale locale, MessageKey key, String value);
}
