package com.hanielcota.essentials.modules.tpa.model;

/** Direction of a teleport request. */
public enum TeleportRequestType {

  /** {@code /tpa} — the requester wants to teleport to the target. */
  TPA,

  /** {@code /tpahere} — the requester wants the target to teleport to them. */
  TPAHERE
}
