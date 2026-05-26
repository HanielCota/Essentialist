package com.hanielcota.essentials.modules.teleport.domain;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Task;

public record PendingTeleport(Task task, DelayedTeleport.Callback callback) {

  public void cancelTask() {
    this.task.cancel();
  }

  public boolean owns(DelayedTeleport.Callback other) {
    return this.callback == other;
  }
}
