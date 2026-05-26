package com.hanielcota.essentials.modules.nick.domain;

import lombok.NonNull;

/**
 * Result of a {@code /nick set} attempt. Variants exhaustively cover the set path so the notifier
 * switch needs no {@code default} arm.
 */
public sealed interface NickSetOutcome {

  /** Nick length is outside {@code [minLength, maxLength]}. */
  record InvalidLength() implements NickSetOutcome {}

  /** Nick contains a non-allowed character. */
  record InvalidChars() implements NickSetOutcome {}

  /** Nick is already owned by another player. */
  record Taken() implements NickSetOutcome {}

  /** Nick was applied and persisted. */
  record Ok(@NonNull String nickname, @NonNull String realName) implements NickSetOutcome {}
}
