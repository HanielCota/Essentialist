package com.hanielcota.essentials.modules.tpa;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import io.github.hanielcota.commandframework.core.ActorKind;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class TpaTestSupport {

  private TpaTestSupport() {}

  public static TeleportRequestService service(
      @NonNull RequestRepository store,
      @NonNull RecordingHistory history,
      @NonNull RecordingPlayerProvider players,
      @NonNull TpaProfileService profiles,
      @NonNull TpaBlockService blocks,
      @NonNull TpaContactService contacts) {
    var config = new StaticConfigHandle();
    return new TeleportRequestService(
        config,
        store,
        history,
        new TpaNotifier(config, players, profiles),
        players,
        profiles,
        blocks,
        contacts);
  }

  public static TpaProfileService profiles() {
    return new TpaProfileService(null, new NoopWriter());
  }

  public static TpaBlockService blocks() {
    return new TpaBlockService(null, new NoopWriter());
  }

  public static TpaContactService contacts() {
    return new TpaContactService(null, new NoopWriter());
  }

  public static Player player(@NonNull TestPlayerState state) {
    var world = world(state.worldId());
    var location = new Location(world, 1, 2, 3);
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (proxy, method, args) -> {
              var name = method.getName();
              if ("getUniqueId".equals(name)) {
                return state.id();
              }
              if ("getName".equals(name)) {
                return state.name();
              }
              if ("getWorld".equals(name)) {
                return world;
              }
              if ("getLocation".equals(name)) {
                return location;
              }
              if ("teleportAsync".equals(name)) {
                state.recordTeleport();
                return CompletableFuture.completedFuture(Boolean.TRUE);
              }
              if ("sendMessage".equals(name)) {
                state.recordMessage();
                return null;
              }
              if ("playSound".equals(name)) {
                state.recordSound();
                return null;
              }
              if ("equals".equals(name)) {
                return proxy == args[0];
              }
              if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
              }
              if ("toString".equals(name)) {
                return state.name();
              }
              return defaultValue(method.getReturnType());
            });
  }

  public static ClickContext click(@NonNull Player player) {
    return (ClickContext)
        Proxy.newProxyInstance(
            ClickContext.class.getClassLoader(),
            new Class<?>[] {ClickContext.class},
            (proxy, method, args) -> {
              var name = method.getName();
              if ("player".equals(name)) {
                return player;
              }
              if ("audience".equals(name)) {
                return Audience.empty();
              }
              return defaultValue(method.getReturnType());
            });
  }

  public static World world(@NonNull UUID id) {
    return (World)
        Proxy.newProxyInstance(
            World.class.getClassLoader(),
            new Class<?>[] {World.class},
            (proxy, method, args) -> {
              if ("getUID".equals(method.getName())) {
                return id;
              }
              return defaultValue(method.getReturnType());
            });
  }

  private static Object defaultValue(@NonNull Class<?> type) {
    if (!type.isPrimitive()) {
      return null;
    }
    if (type == boolean.class) {
      return false;
    }
    if (type == void.class) {
      return null;
    }
    if (type == char.class) {
      return '\0';
    }
    return 0;
  }

  public static final class TestPlayerState {
    private final UUID id;
    private final String name;
    private final UUID worldId;
    private int messages;
    private int sounds;
    private int teleports;

    public TestPlayerState(@NonNull String name) {
      this(UUID.randomUUID(), name, UUID.randomUUID());
    }

    public TestPlayerState(@NonNull UUID id, @NonNull String name, @NonNull UUID worldId) {
      this.id = id;
      this.name = name;
      this.worldId = worldId;
    }

    public UUID id() {
      return this.id;
    }

    public String name() {
      return this.name;
    }

    public UUID worldId() {
      return this.worldId;
    }

    public int messages() {
      return this.messages;
    }

    public int sounds() {
      return this.sounds;
    }

    public int teleports() {
      return this.teleports;
    }

    private void recordMessage() {
      this.messages++;
    }

    private void recordSound() {
      this.sounds++;
    }

    private void recordTeleport() {
      this.teleports++;
    }
  }

  public static final class RecordingActor implements CommandActor {
    private final Player player;
    private final List<String> messages = new ArrayList<>();

    RecordingActor(@NonNull Player player) {
      this.player = player;
    }

    public List<String> messages() {
      return List.copyOf(this.messages);
    }

    @Override
    public String uniqueId() {
      return this.player.getUniqueId().toString();
    }

    @Override
    public String name() {
      return this.player.getName();
    }

    @Override
    public ActorKind kind() {
      return ActorKind.PLAYER;
    }

    @Override
    public boolean hasPermission(String permission) {
      return true;
    }

    @Override
    public void sendMessage(String message) {
      this.messages.add(message);
    }

    @Override
    public void sendMessage(Component component) {
      this.messages.add(component.toString());
    }

    @Override
    public boolean is(Class<?> type) {
      return type.isInstance(this.player);
    }

    @Override
    public <T> T as(Class<T> type) {
      return type.cast(this.player);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return as(type);
    }
  }

  public static final class RecordingActorFactory implements ActorFactory {
    private final Map<UUID, RecordingActor> actors = new HashMap<>();

    public RecordingActor actor(@NonNull Player player) {
      return this.actors.get(player.getUniqueId());
    }

    @Override
    public CommandActor actorOf(@NonNull Player player) {
      return this.actors.computeIfAbsent(player.getUniqueId(), id -> new RecordingActor(player));
    }
  }

  public static final class RecordingPlayerProvider implements PlayerProvider {
    private final Map<UUID, Player> players = new HashMap<>();

    public void add(@NonNull Player player) {
      this.players.put(player.getUniqueId(), player);
    }

    @Override
    public Optional<Player> online(@NonNull UUID id) {
      return Optional.ofNullable(this.players.get(id));
    }

    @Override
    public Optional<Player> online(@NonNull String name) {
      return this.players.values().stream()
          .filter(player -> player.getName().equalsIgnoreCase(name))
          .findFirst();
    }

    @Override
    public OfflinePlayer offline(@NonNull UUID id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OfflinePlayer> offlineByName(@NonNull String name) {
      return Optional.empty();
    }

    @Override
    public Collection<Player> all() {
      return List.copyOf(this.players.values());
    }

    @Override
    public int maxPlayers() {
      return this.players.size();
    }

    @Override
    public Collection<OfflinePlayer> whitelisted() {
      return List.of();
    }
  }

  public static final class RecordingHistory implements TpaHistory {
    private final List<TpaHistoryEntry> entries = new ArrayList<>();

    public List<TpaHistoryEntry> entries() {
      return List.copyOf(this.entries);
    }

    @Override
    public void push(@NonNull TpaHistoryEntry entry) {
      this.entries.add(entry);
    }

    @Override
    public List<TpaHistoryEntry> list(@NonNull UUID requester) {
      return this.entries.stream().filter(entry -> entry.requester().equals(requester)).toList();
    }
  }

  public static final class DirectScheduler implements Scheduler {
    @Override
    public void runSync(@NonNull Runnable task) {
      task.run();
    }

    @Override
    public void runAsync(@NonNull Runnable task) {
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
      return Task.noop();
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

    @Override
    public Task runAsyncLater(@NonNull Runnable task, @NonNull Duration delay) {
      return Task.noop();
    }

    @Override
    public Task runAsyncTimer(
        @NonNull Runnable task, @NonNull Duration initialDelay, @NonNull Duration period) {
      return Task.noop();
    }
  }

  public static final class StaticConfigHandle implements ConfigHandle<TpaConfig> {
    @Override
    public String name() {
      return "tpa";
    }

    @Override
    public TpaConfig value() {
      return TpaConfig.defaults();
    }

    @Override
    public void reload() {
      // no-op test stub
    }

    @Override
    public AutoCloseable onReload(@NonNull java.util.function.Consumer<TpaConfig> listener) {
      return () -> {};
    }
  }

  public static final class NoopWriter implements AsyncDatabaseWriter {
    @Override
    public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
      work.run();
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() {
      // no-op test stub
    }
  }
}
