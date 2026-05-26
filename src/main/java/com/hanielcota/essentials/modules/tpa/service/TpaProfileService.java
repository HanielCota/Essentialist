package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class TpaProfileService {

  private final @Nullable TpaProfileRepository repository;
  private final AsyncDatabaseWriter writer;
  private final Map<UUID, TpaProfile> profiles = new ConcurrentHashMap<>();

  public TpaProfileService(
      @Nullable TpaProfileRepository repository, @NonNull AsyncDatabaseWriter writer) {
    this.repository = repository;
    this.writer = writer;
  }

  private static TpaProfile applyOrDefault(
      @Nullable TpaProfile current, @NonNull UnaryOperator<TpaProfile> mutator) {
    var base = current != null ? current : TpaProfile.defaults();
    return mutator.apply(base);
  }

  public void loadAll(@NonNull List<Entry> entries) {
    for (var entry : entries) {
      this.profiles.put(entry.playerId(), entry.profile());
    }
  }

  public TpaProfile profile(@NonNull UUID playerId) {
    return this.profiles.getOrDefault(playerId, TpaProfile.defaults());
  }

  public TpaProfile toggle(@NonNull UUID playerId, @NonNull TeleportRequestType type) {
    return mutate(playerId, current -> current.toggled(type));
  }

  public TpaProfile recordSent(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::incrementSentRequests);
  }

  public TpaProfile recordReceived(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::incrementReceivedRequests);
  }

  public TpaProfile recordAcceptedOutgoing(@NonNull UUID playerId, @NonNull Duration latency) {
    return mutate(playerId, current -> current.recordAcceptedOutgoing(latency));
  }

  public TpaProfile toggleAutoAcceptFavorites(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::toggledAutoAcceptFavorites);
  }

  public TpaProfile toggleSounds(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::toggledSounds);
  }

  public TpaProfile toggleAllowCrossWorld(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::toggledAllowCrossWorld);
  }

  public TpaProfile toggleNotifyWhenFavorited(@NonNull UUID playerId) {
    return mutate(playerId, TpaProfile::toggledNotifyWhenFavorited);
  }

  public TpaProfile setDndUntil(@NonNull UUID playerId, long epochMs) {
    return mutate(playerId, current -> current.withDndUntil(epochMs));
  }

  public TpaProfile cycleFavoriteOrdering(@NonNull UUID playerId) {
    return mutate(
        playerId,
        current -> {
          var nextOrdering = current.favoriteOrdering().next();
          return current.withFavoriteOrdering(nextOrdering);
        });
  }

  public TpaProfile setFavoriteOrdering(
      @NonNull UUID playerId, @NonNull FavoriteOrdering ordering) {
    return mutate(playerId, current -> current.withFavoriteOrdering(ordering));
  }

  public boolean accepts(@NonNull UUID playerId, @NonNull TeleportRequestType type) {
    var profile = profile(playerId);

    return profile.accepts(type);
  }

  public boolean isDndActive(@NonNull UUID playerId) {
    var profile = profile(playerId);

    return profile.isDndActive(System.currentTimeMillis());
  }

  private TpaProfile mutate(@NonNull UUID playerId, @NonNull UnaryOperator<TpaProfile> mutator) {
    var updated =
        this.profiles.compute(playerId, (id, current) -> applyOrDefault(current, mutator));

    save(playerId, updated);
    return updated;
  }

  private void save(@NonNull UUID playerId, @NonNull TpaProfile profile) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit("save-tpa-profile", () -> this.repository.save(playerId, profile));
  }

  public record Entry(@NonNull UUID playerId, @NonNull TpaProfile profile) {}
}
