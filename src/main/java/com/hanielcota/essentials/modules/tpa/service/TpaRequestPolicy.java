package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
final class TpaRequestPolicy {

  private final @NonNull TpaProfileService profiles;
  private final @NonNull TpaBlockService blocks;

  boolean canCreate(
      @NonNull Player requester, @NonNull Player target, @NonNull TeleportRequestType type) {
    var targetId = target.getUniqueId();
    var requesterId = requester.getUniqueId();

    if (!this.profiles.accepts(targetId, type)) {
      return false;
    }

    if (this.blocks.isBlocked(targetId, requesterId)) {
      return false;
    }

    if (this.profiles.isDndActive(targetId)) {
      return false;
    }

    return !isCrossWorldRefused(requester, target);
  }

  boolean isBlockedBy(@NonNull UUID blockerId, @NonNull UUID requesterId) {
    return this.blocks.isBlocked(blockerId, requesterId);
  }

  boolean isDndActive(@NonNull UUID targetId) {
    return this.profiles.isDndActive(targetId);
  }

  boolean isCrossWorldRefused(@NonNull Player requester, @NonNull Player target) {
    var targetId = target.getUniqueId();
    var targetProfile = this.profiles.profile(targetId);
    if (targetProfile.allowCrossWorld()) {
      return false;
    }

    var requesterWorldId = requester.getWorld().getUID();
    var targetWorldId = target.getWorld().getUID();

    return !requesterWorldId.equals(targetWorldId);
  }
}
