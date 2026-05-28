package com.hanielcota.essentials.modules.homes.service;

import lombok.NonNull;

/**
 * Shared cancellation vocabulary for the home chat prompts (create + rename). Centralised so a
 * future keyword change happens in exactly one place.
 */
public final class HomePromptCancellation {

  private HomePromptCancellation() {}

  public static boolean isCancel(@NonNull String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }
}
