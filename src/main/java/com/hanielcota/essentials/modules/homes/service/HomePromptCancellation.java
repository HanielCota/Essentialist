package com.hanielcota.essentials.modules.homes.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Shared cancellation vocabulary for the home chat prompts (create + rename). Centralised so a
 * future keyword change happens in exactly one place.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HomePromptCancellation {

  public static boolean isCancel(@NonNull String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }
}
