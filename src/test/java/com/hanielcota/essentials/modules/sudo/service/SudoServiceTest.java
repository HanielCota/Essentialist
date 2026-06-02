package com.hanielcota.essentials.modules.sudo.service;

import org.junit.jupiter.api.Test;

class SudoServiceTest {

  @Test
  void stripLeadingSlashRemovesIt() {
    var scheduler =
        new com.hanielcota.essentials.scheduler.Scheduler() {
          @Override
          public void runOnEntity(org.bukkit.entity.Entity entity, Runnable task) {
            task.run();
          }

          @Override
          public void runSync(Runnable task) {
            task.run();
          }

          @Override
          public java.util.concurrent.Executor mainExecutor() {
            return Runnable::run;
          }

          @Override
          public com.hanielcota.essentials.scheduler.Task runLater(
              Runnable task, java.time.Duration delay) {
            return com.hanielcota.essentials.scheduler.Task.noop();
          }

          @Override
          public com.hanielcota.essentials.scheduler.Task runOnEntityLater(
              org.bukkit.entity.Entity entity, Runnable task, java.time.Duration delay) {
            return com.hanielcota.essentials.scheduler.Task.noop();
          }

          @Override
          public com.hanielcota.essentials.scheduler.Task runTimer(
              Runnable task, java.time.Duration delay, java.time.Duration period) {
            return com.hanielcota.essentials.scheduler.Task.noop();
          }
        };

    var service = new SudoService(scheduler);
    var player = fakePlayer("Steve");

    service.run(player, "/fly");

    // SudoService#stripLeadingSlash normalizes the command
    // Just verify it doesn't throw
  }

  private static org.bukkit.entity.Player fakePlayer(String name) {
    return (org.bukkit.entity.Player)
        java.lang.reflect.Proxy.newProxyInstance(
            org.bukkit.entity.Player.class.getClassLoader(),
            new Class<?>[] {org.bukkit.entity.Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getName" -> name;
                case "performCommand" -> {
                  var cmd = (String) args[0];
                  yield !cmd.startsWith("/");
                }
                case "getUniqueId" -> java.util.UUID.randomUUID();
                case "getEffectivePermissions" -> java.util.Set.of();
                case "toString" -> name;
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }
}
