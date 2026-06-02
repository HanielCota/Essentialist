package com.hanielcota.essentials.modules.back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class BackWarmupServiceTest {

  @Test
  void replacingWarmupCancelsPreviousTaskAndOnlyLatestCanComplete() {
    var scheduler = new RecordingScheduler();
    var service = new BackWarmupService(scheduler);
    var player = player(UUID.randomUUID());
    var completed = new ArrayList<String>();
    var cancelled = new ArrayList<String>();

    service.schedule(
        player, Duration.ofSeconds(1), () -> completed.add("first"), () -> cancelled.add("first"));
    service.schedule(
        player,
        Duration.ofSeconds(1),
        () -> completed.add("second"),
        () -> cancelled.add("second"));

    assertEquals(List.of("first"), cancelled);
    assertTrue(scheduler.tasks.getFirst().isCancelled());

    scheduler.run(0);
    scheduler.run(1);

    assertEquals(List.of("second"), completed);
  }

  private static Player player(@NonNull UUID uuid) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (proxy, method, args) -> {
              var name = method.getName();
              return switch (name) {
                case "getUniqueId" -> uuid;
                case "hashCode" -> uuid.hashCode();
                case "equals" -> proxy == args[0];
                case "toString" -> "Player(" + uuid + ")";
                default -> throw new UnsupportedOperationException(name);
              };
            });
  }

  private static final class RecordingScheduler implements Scheduler {

    private final List<RecordingTask> tasks = new ArrayList<>();

    @Override
    public void runSync(@NonNull Runnable task) {
      task.run();
    }

    @Override
    public Executor mainExecutor() {
      return Runnable::run;
    }

    @Override
    public void runOnEntity(@NonNull Entity entity, @NonNull Runnable task) {
      task.run();
    }

    @Override
    public Task runOnEntityLater(
        @NonNull Entity entity, @NonNull Runnable task, @NonNull Duration delay) {
      var scheduled = new RecordingTask(task);
      this.tasks.add(scheduled);
      return scheduled;
    }

    @Override
    public Task runLater(@NonNull Runnable task, @NonNull Duration delay) {
      return Task.noop();
    }

    @Override
    public Task runTimer(
        @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
      return Task.noop();
    }

    void run(int index) {
      this.tasks.get(index).run();
    }
  }

  private static final class RecordingTask implements Task {

    private final Runnable task;
    private boolean cancelled;

    private RecordingTask(@NonNull Runnable task) {
      this.task = task;
    }

    void run() {
      this.task.run();
    }

    @Override
    public void cancel() {
      this.cancelled = true;
    }

    @Override
    public boolean isCancelled() {
      return this.cancelled;
    }
  }
}
