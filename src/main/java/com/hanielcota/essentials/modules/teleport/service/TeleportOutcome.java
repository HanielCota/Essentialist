package com.hanielcota.essentials.modules.teleport.service;

/**
 * Result of a teleport attempt. Pre-flight rejections ({@link SelfTarget}, {@link InvalidPosition})
 * complete the future immediately; the {@link Success}/{@link Failed} pair is driven by Paper's
 * {@code teleportAsync} completion.
 */
public sealed interface TeleportOutcome {

  /** Teleport landed and {@code teleportAsync} reported success. */
  record Success() implements TeleportOutcome {}

  /** Teleport call was made but {@code teleportAsync} returned {@code false}. */
  record Failed() implements TeleportOutcome {}

  /** Pre-flight reject: sender and target are the same player. */
  record SelfTarget() implements TeleportOutcome {}

  /** Pre-flight reject: coordinates are outside world limits or the world border. */
  record InvalidPosition() implements TeleportOutcome {}
}
