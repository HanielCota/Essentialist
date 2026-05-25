package com.hanielcota.essentials.modules.nick.service;

import lombok.NonNull;

/**
 * Result of a {@code /nick} operation (set or reset). The command routes each variant to {@code
 * NickNotifier} without inlining domain branching.
 */
public sealed interface NickOutcome {

  /** Reset attempted but the subject already has no nick. */
  record AlreadyHasNoNick() implements NickOutcome {}

  /** Set attempted with a length outside [minLength, maxLength]. */
  record InvalidLength() implements NickOutcome {}

  /** Set attempted with a non-allowed character. */
  record InvalidChars() implements NickOutcome {}

  /** Set attempted on a nick already owned by another player. */
  record Taken() implements NickOutcome {}

  /** Set succeeded — applied and persisted. */
  record SetOk(@NonNull String nickname, @NonNull String realName) implements NickOutcome {}

  /** Reset succeeded — applier restored the real name. */
  record ResetOk() implements NickOutcome {}
}
