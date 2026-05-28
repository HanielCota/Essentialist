package com.hanielcota.essentials.modules.tpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.modules.tpa.repository.InMemoryRequestRepository;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class TeleportRequestServiceTest {

  @Test
  void createReturnsEmptyWhenTargetBlocksTpa() {
    var profiles = newProfileService();
    var targetId = UUID.randomUUID();
    profiles.toggle(targetId, TeleportRequestType.TPA);

    var service = newService(profiles);
    var requester = player(UUID.randomUUID(), "Alice");
    var target = player(targetId, "Bob");

    var created = service.create(requester, target, TeleportRequestType.TPA);

    assertTrue(created.isEmpty());
  }

  @Test
  void createReturnsEmptyWhenTargetBlocksTpaHere() {
    var profiles = newProfileService();
    var targetId = UUID.randomUUID();
    profiles.toggle(targetId, TeleportRequestType.TPAHERE);

    var service = newService(profiles);
    var requester = player(UUID.randomUUID(), "Alice");
    var target = player(targetId, "Bob");

    var created = service.create(requester, target, TeleportRequestType.TPAHERE);

    assertTrue(created.isEmpty());
  }

  @Test
  void createRecordsSentForRequesterAndReceivedForTarget() {
    var profiles = newProfileService();
    var service = newService(profiles);
    var requesterId = UUID.randomUUID();
    var targetId = UUID.randomUUID();
    var requester = player(requesterId, "Alice");
    var target = player(targetId, "Bob");

    var created = service.create(requester, target, TeleportRequestType.TPA);

    assertTrue(created.isPresent());
    assertEquals(1, profiles.profile(requesterId).sentRequests());
    assertEquals(0, profiles.profile(requesterId).receivedRequests());
    assertEquals(0, profiles.profile(targetId).sentRequests());
    assertEquals(1, profiles.profile(targetId).receivedRequests());
  }

  @Test
  void createReturnsEmptyWhenTargetBlockedRequester() {
    var blocks = newBlockService();
    var requesterId = UUID.randomUUID();
    var targetId = UUID.randomUUID();
    blocks.block(targetId, requesterId, "Alice");

    var service = newService(newProfileService(), blocks);
    var requester = player(requesterId, "Alice");
    var target = player(targetId, "Bob");

    var created = service.create(requester, target, TeleportRequestType.TPA);

    assertTrue(created.isEmpty());
  }

  private static TpaProfileService newProfileService() {
    return new TpaProfileService(null, NoopAsyncDatabaseWriter.INSTANCE);
  }

  private static TpaBlockService newBlockService() {
    return new TpaBlockService(null, NoopAsyncDatabaseWriter.INSTANCE);
  }

  private static TpaContactService newContactService() {
    return new TpaContactService(null, NoopAsyncDatabaseWriter.INSTANCE);
  }

  private static TeleportRequestService newService(@NonNull TpaProfileService profiles) {
    return newService(profiles, newBlockService());
  }

  private static TeleportRequestService newService(
      @NonNull TpaProfileService profiles, @NonNull TpaBlockService blocks) {
    var favorites = new TpaFavoriteService(null, NoopAsyncDatabaseWriter.INSTANCE);
    var players = new EmptyPlayerProvider();
    var policy = new TpaRequestPolicy(profiles, blocks);
    var recorder = new TpaRequestRecorder(new NoopHistory(), profiles, newContactService());
    var executor = new TeleportRequestExecutor(players);
    return new TeleportRequestService(
        new StaticConfigHandle(),
        new InMemoryRequestRepository(),
        players,
        policy,
        recorder,
        executor);
  }

  private static Player player(@NonNull UUID id, @NonNull String name) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (proxy, method, args) ->
                switch (method.getName()) {
                  case "getUniqueId" -> id;
                  case "getName" -> name;
                  case "sendMessage", "playSound", "getLocation", "getWorld" -> null;
                  default -> throw new UnsupportedOperationException(method.getName());
                });
  }

  private static final class StaticConfigHandle implements ConfigHandle<TpaConfig> {
    @Override
    public String name() {
      return "tpa";
    }

    @Override
    public TpaConfig value() {
      return TpaConfig.defaults();
    }
  }

  private static final class NoopHistory implements TpaHistory {
    @Override
    public void push(@NonNull TpaHistoryEntry entry) {}

    @Override
    public List<TpaHistoryEntry> list(@NonNull UUID requester) {
      return List.of();
    }
  }

  private static final class EmptyPlayerProvider implements PlayerProvider {
    @Override
    public Optional<Player> online(@NonNull UUID id) {
      return Optional.empty();
    }

    @Override
    public Optional<Player> online(@NonNull String name) {
      return Optional.empty();
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
      return List.of();
    }

    @Override
    public int maxPlayers() {
      return 0;
    }

    @Override
    public Collection<OfflinePlayer> whitelisted() {
      return List.of();
    }
  }
}
