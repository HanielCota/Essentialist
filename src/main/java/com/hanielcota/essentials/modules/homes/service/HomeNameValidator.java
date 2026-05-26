package com.hanielcota.essentials.modules.homes.service;

import java.util.regex.Pattern;
import lombok.NonNull;

/** Default rule set: 1-32 chars, ASCII letters/numbers plus '_' and '-'. */
public final class HomeNameValidator {

  private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9_-]{1,32}");

  public boolean isValid(@NonNull String name) {
    return SAFE_NAME.matcher(name).matches();
  }
}
