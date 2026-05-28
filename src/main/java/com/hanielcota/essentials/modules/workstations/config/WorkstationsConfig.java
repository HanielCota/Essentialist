package com.hanielcota.essentials.modules.workstations.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WorkstationsConfig(
    @Comment("Shown when a non-player runs a workstation command.") String playerOnly) {

  public static WorkstationsConfig defaults() {
    return new WorkstationsConfig("<red>Este comando so pode ser executado por jogadores.");
  }
}
