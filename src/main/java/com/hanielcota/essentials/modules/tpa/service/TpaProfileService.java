package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

  public void loadAll(@NonNull List<Entry> entries) {
    for (var entry : entries) {
      this.profiles.put(entry.playerId(), entry.profile());
    }
  }

  public TpaProfile profile(@NonNull UUID playerId) {
    return this.profiles.getOrDefault(playerId, TpaProfile.defaults());
  }

  public TpaProfile toggle(@NonNull UUID playerId, @NonNull TeleportRequestType type) {
    var updated = this.profiles.compute(playerId, (id, current) -> toggled(current, type));

    save(playerId, updated);
    return updated;
  }

  public TpaProfile recordSent(@NonNull UUID playerId) {
    var updated = this.profiles.compute(playerId, (id, current) -> incremented(current));

    save(playerId, updated);
    return updated;
  }

  public TpaProfile recordReceived(@NonNull UUID playerId) {
    var updated = this.profiles.compute(playerId, (id, current) -> receivedIncremented(current));

    save(playerId, updated);
    return updated;
  }

  public boolean accepts(@NonNull UUID playerId, @NonNull TeleportRequestType type) {
    var profile = profile(playerId);

    return profile.accepts(type);
  }

  private static TpaProfile toggled(
      @Nullable TpaProfile current, @NonNull TeleportRequestType type) {
    var base = current != null ? current : TpaProfile.defaults();

    return base.toggled(type);
  }

  private static TpaProfile incremented(@Nullable TpaProfile current) {
    var base = current != null ? current : TpaProfile.defaults();

    return base.incrementSentRequests();
  }

  private static TpaProfile receivedIncremented(@Nullable TpaProfile current) {
    var base = current != null ? current : TpaProfile.defaults();

    return base.incrementReceivedRequests();
  }

  private void save(@NonNull UUID playerId, @NonNull TpaProfile profile) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit("save-tpa-profile", () -> this.repository.save(playerId, profile));
  }

  public record Entry(@NonNull UUID playerId, @NonNull TpaProfile profile) {}
}
