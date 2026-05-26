package com.hanielcota.essentials.modules.tpa.domain;

import lombok.NonNull;
import org.bukkit.entity.Player;

/** Direction of a teleport request. */
public enum TeleportRequestType {

  /** {@code /tpa} — the requester wants to teleport to the target. */
  TPA {
    @Override
    public Player mover(@NonNull Player requester, @NonNull Player target) {
      return requester;
    }

    @Override
    public Player destination(@NonNull Player requester, @NonNull Player target) {
      return target;
    }
  },

  /** {@code /tpahere} — the requester wants the target to teleport to them. */
  TPAHERE {
    @Override
    public Player mover(@NonNull Player requester, @NonNull Player target) {
      return target;
    }

    @Override
    public Player destination(@NonNull Player requester, @NonNull Player target) {
      return requester;
    }
  };

  public abstract Player mover(@NonNull Player requester, @NonNull Player target);

  public abstract Player destination(@NonNull Player requester, @NonNull Player target);
}
