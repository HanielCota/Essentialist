package com.hanielcota.essentials.config;

/**
 * Marker interface for configs that follow the self/other message pattern.
 *
 * <p>Implementations expose {@link MessagePair} instances for each outcome (success, failure,
 * toggle, etc.) eliminating the repetitive {@code xxxFor(boolean selfTarget, String player)}
 * methods.
 */
public interface TargetedConfig {}
