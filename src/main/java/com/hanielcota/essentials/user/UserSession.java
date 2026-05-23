package com.hanielcota.essentials.user;

import java.time.Instant;
import java.util.UUID;

/** Tracks when a player connected, so features like /info can show session uptime. */
public record UserSession(UUID playerId, Instant connectedAt) {}
