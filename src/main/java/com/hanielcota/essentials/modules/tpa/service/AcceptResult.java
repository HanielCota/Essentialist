package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;

/** Outcome of a {@link TeleportRequestService#accept(TeleportRequest)} call. */
public enum AcceptResult {
  SUCCESS,
  NOT_FOUND,
  REQUESTER_OFFLINE,
  TELEPORT_FAILED
}
