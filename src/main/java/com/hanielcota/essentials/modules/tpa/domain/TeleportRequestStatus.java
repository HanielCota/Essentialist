package com.hanielcota.essentials.modules.tpa.domain;

/** The terminal outcome of a resolved teleport request, as recorded in history. */
public enum TeleportRequestStatus {
  ACCEPTED,
  DENIED,
  EXPIRED,
  CANCELLED
}
