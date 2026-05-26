package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class TeleportsApiAdapter implements TeleportsApi {

  private final TeleportService service;

  @Override
  public CompletableFuture<TeleportOutcome> toPlayer(@NonNull Player from, @NonNull Player to) {
    return this.service.toPlayer(from, to);
  }

  @Override
  public CompletableFuture<TeleportOutcome> toPosition(
      @NonNull Player subject, double x, double y, double z) {
    return this.service.toPosition(subject, x, y, z);
  }
}
