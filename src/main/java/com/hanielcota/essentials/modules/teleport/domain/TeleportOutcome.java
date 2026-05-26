package com.hanielcota.essentials.modules.teleport.domain;

/**
 * Result of a teleport attempt. Pre-flight rejections ({@link #SELF_TARGET}, {@link
 * #INVALID_POSITION}) complete the future immediately; {@link #SUCCESS}/{@link #FAILED} are driven
 * by Paper's {@code teleportAsync} completion.
 */
public enum TeleportOutcome {
  SUCCESS,
  FAILED,
  SELF_TARGET,
  INVALID_POSITION
}
