package com.hanielcota.essentials.modules.homes.name;

import java.util.regex.Pattern;

/** Default rule set: 1-32 chars, ASCII letters/numbers plus '_' and '-'. */
public final class DefaultHomeNameValidator implements HomeNameValidator {

  private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9_-]{1,32}");

  @Override
  public boolean isValid(String name) {
    return SAFE_NAME.matcher(name).matches();
  }
}
