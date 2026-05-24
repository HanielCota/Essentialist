package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.scheduler.Task;

record PendingTeleport(Task task, DelayedTeleport.Callback callback) {

  void cancelTask() {
    this.task.cancel();
  }

  boolean owns(DelayedTeleport.Callback other) {
    return this.callback == other;
  }
}
