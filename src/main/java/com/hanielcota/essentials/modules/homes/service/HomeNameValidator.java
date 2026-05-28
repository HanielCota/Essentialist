package com.hanielcota.essentials.modules.homes.service;

import java.util.regex.Pattern;
import lombok.NonNull;

/** Validates home names against a configurable length range and character pattern. */
public final class HomeNameValidator {

  private static final int DEFAULT_MIN_LENGTH = 1;
  private static final int DEFAULT_MAX_LENGTH = 32;
  private static final String DEFAULT_PATTERN = "[A-Za-z0-9_-]+";

  private final int minLength;
  private final int maxLength;
  private final Pattern pattern;

  /** Default rule set: 1-32 chars, ASCII letters/numbers plus '_' and '-'. */
  public HomeNameValidator() {
    this(DEFAULT_MIN_LENGTH, DEFAULT_MAX_LENGTH, DEFAULT_PATTERN);
  }

  public HomeNameValidator(int minLength, int maxLength, @NonNull String pattern) {
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.pattern = Pattern.compile(pattern);
  }

  public boolean isValid(@NonNull String name) {
    var length = name.length();
    if (length < this.minLength || length > this.maxLength) {
      return false;
    }

    return this.pattern.matcher(name).matches();
  }
}
