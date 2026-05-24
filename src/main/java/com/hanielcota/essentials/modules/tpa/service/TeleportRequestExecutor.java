package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import lombok.NonNull;
import org.bukkit.Bukkit;

final class TeleportRequestExecutor {

  TeleportExecution execute(@NonNull TeleportRequest request) {
    var requester = Bukkit.getPlayer(request.requester().id());
    var target = Bukkit.getPlayer(request.target().id());
    if (requester == null || target == null) {
      return TeleportExecution.failed(AcceptResult.REQUESTER_OFFLINE);
    }

    var toTarget = request.type() == TeleportRequestType.TPA;
    var mover = toTarget ? requester : target;
    var benchmark = toTarget ? target : requester;
    var landing = benchmark.getLocation();

    if (!mover.teleport(landing)) {
      return TeleportExecution.failed(AcceptResult.TELEPORT_FAILED);
    }

    return TeleportExecution.success(Destination.of(landing));
  }
}
