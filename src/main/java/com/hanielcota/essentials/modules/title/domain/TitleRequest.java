package com.hanielcota.essentials.modules.title.domain;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * The recipient and message parsed from {@code /title} input. The target is identified by {@link
 * UUID} + name snapshot rather than a live {@code Player} reference so the record stays valid past
 * the parse tick and the command path stays safe even if the player disconnects between parse and
 * dispatch.
 *
 * <p>Parsing rules live in {@code TitleRequestParser}; this record is a pure value carrier.
 */
public record TitleRequest(@Nullable UUID targetId, @Nullable String targetName, String message) {}
