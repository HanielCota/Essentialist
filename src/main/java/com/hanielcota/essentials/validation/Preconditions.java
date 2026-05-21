package com.hanielcota.essentials.validation;

public final class Preconditions {

  private Preconditions() {}

  public static <T> T notNull(T value, String name) {
    if (value == null) {
      throw new ValidationException(name + " must not be null");
    }
    return value;
  }

  public static String notBlank(String value, String name) {
    notNull(value, name);
    if (value.isBlank()) {
      throw new ValidationException(name + " must not be blank");
    }
    return value;
  }

  public static int positive(int value, String name) {
    if (value <= 0) {
      throw new ValidationException(name + " must be positive: " + value);
    }
    return value;
  }

  public static long positive(long value, String name) {
    if (value <= 0L) {
      throw new ValidationException(name + " must be positive: " + value);
    }
    return value;
  }

  public static void check(boolean condition, String message) {
    if (!condition) {
      throw new ValidationException(message);
    }
  }
}
