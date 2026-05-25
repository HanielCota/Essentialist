package com.hanielcota.essentials.modules.nick.service;

/**
 * Result of a {@code /nick off} attempt. Variants exhaustively cover the reset path so the notifier
 * switch needs no {@code default} arm.
 */
public sealed interface NickResetOutcome {

  /** The subject already had no nick. */
  record AlreadyHasNoNick() implements NickResetOutcome {}

  /** The applier restored the real name. */
  record Ok() implements NickResetOutcome {}
}
