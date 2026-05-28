package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;

/**
 * Wraps the chat-feedback callback so that, on a successful warp teleport, the player is recorded
 * in {@link WarpOccupancy}. Every other lifecycle hook is delegated untouched.
 */
public record WarpArrivalCallback(
    @NonNull DelayedTeleport.Callback delegate,
    @NonNull WarpOccupancy occupancy,
    @NonNull UUID playerId,
    @NonNull Warp warp,
    @NonNull Location destination)
    implements DelayedTeleport.Callback {

  @Override
  public void onScheduled(long seconds) {
    this.delegate.onScheduled(seconds);
  }

  @Override
  public void onSuccess() {
    this.delegate.onSuccess();

    var worldName = this.destination.getWorld().getName();
    this.occupancy.enter(
        this.playerId,
        this.warp.name(),
        worldName,
        this.destination.getX(),
        this.destination.getY(),
        this.destination.getZ());
  }

  @Override
  public void onCancelled() {
    this.delegate.onCancelled();
  }

  @Override
  public void onFailed() {
    this.delegate.onFailed();
  }
}
