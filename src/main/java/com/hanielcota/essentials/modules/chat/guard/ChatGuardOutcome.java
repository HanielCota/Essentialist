package com.hanielcota.essentials.modules.chat.guard;

/**
 * Outcome of a single {@link ChatGuardCheck}. {@code ALLOW} lets the pipeline move on; {@code
 * BLOCK} short-circuits and the listener (or command) must cancel the broadcast. The check itself
 * is responsible for warning the sender — {@code BLOCK} just means "stop here".
 */
public enum ChatGuardOutcome {
  ALLOW,
  BLOCK
}
