package com.hanielcota.essentials.command;

import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Reusable {@link UnaryOperator} line transformers for {@code DualReply} templates. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Replacers {

  /** Swaps the {@code {count}} token for {@code count} in each line. */
  public static UnaryOperator<String> count(@NonNull String count) {
    return line -> line.replace("{count}", count);
  }
}
