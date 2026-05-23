package com.hanielcota.essentials.modules.homes.rename;

/** Default rule set: 1-32 chars, no embedded spaces. */
public final class DefaultHomeNameValidator implements HomeNameValidator {

  private static final int MAX_LENGTH = 32;

  @Override
  public boolean isValid(String name) {
    return !name.isEmpty() && name.length() <= MAX_LENGTH && !name.contains(" ");
  }
}
