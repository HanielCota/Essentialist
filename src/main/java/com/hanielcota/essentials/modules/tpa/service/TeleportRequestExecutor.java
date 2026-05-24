package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.Bukkit;

final class TeleportRequestExecutor {

  CompletableFuture<TeleportExecution> execute(@NonNull TeleportRequest request) {
    var requester = Bukkit.getPlayer(request.requester().id());
    var target = Bukkit.getPlayer(request.target().id());
    if (requester == null || target == null) {
      return CompletableFuture.completedFuture(
          TeleportExecution.failed(AcceptResult.REQUESTER_OFFLINE));
    }

    var toTarget = request.type() == TeleportRequestType.TPA;
    var mover = toTarget ? requester : target;
    var benchmark = toTarget ? target : requester;
    var landing = benchmark.getLocation();

    return mover
        .teleportAsync(landing)
        .thenApply(
            success -> {
              if (Boolean.TRUE.equals(success)) {
                return TeleportExecution.success(Destination.of(landing));
              }
              return TeleportExecution.failed(AcceptResult.TELEPORT_FAILED);
            });
  }
}
