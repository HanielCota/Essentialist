package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.repository.TpaContactRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Aggregates successful outgoing TPAs per owner→target pair. Incremented every time an outgoing
 * request finishes ACCEPTED and surfaces "top contacted" and "last contacted" reads to the menus.
 */
public final class TpaContactService {

  private final @Nullable TpaContactRepository repository;
  private final AsyncDatabaseWriter writer;
  private final Map<UUID, Map<UUID, TpaContact>> contacts = new ConcurrentHashMap<>();

  public TpaContactService(
      @Nullable TpaContactRepository repository, @NonNull AsyncDatabaseWriter writer) {
    this.repository = repository;
    this.writer = writer;
  }

  private static TpaContact bumped(
      @Nullable TpaContact prior,
      @NonNull UUID ownerId,
      @NonNull UUID targetId,
      @NonNull String targetName,
      long now) {
    if (prior == null) {
      return new TpaContact(ownerId, targetId, targetName, 1, now);
    }

    var nextCount = prior.count() + 1;
    return new TpaContact(ownerId, targetId, targetName, nextCount, now);
  }

  public void loadAll(@NonNull List<TpaContact> entries) {
    for (var entry : entries) {
      var ownerContacts =
          this.contacts.computeIfAbsent(entry.ownerId(), id -> new ConcurrentHashMap<>());
      ownerContacts.put(entry.targetId(), entry);
    }
  }

  /** Records one successful outgoing TPA from {@code ownerId} to {@code (targetId, targetName)}. */
  public TpaContact recordContact(
      @NonNull UUID ownerId, @NonNull UUID targetId, @NonNull String targetName) {
    var ownerContacts = this.contacts.computeIfAbsent(ownerId, id -> new ConcurrentHashMap<>());
    var now = System.currentTimeMillis();

    var updated =
        ownerContacts.compute(
            targetId, (id, prior) -> bumped(prior, ownerId, targetId, targetName, now));

    save(updated);
    return updated;
  }

  public List<TpaContact> top(@NonNull UUID ownerId, int limit) {
    var ownerContacts = this.contacts.getOrDefault(ownerId, Map.of());
    return ownerContacts.values().stream()
        .sorted(
            Comparator.comparingLong(TpaContact::count)
                .reversed()
                .thenComparing(Comparator.comparingLong(TpaContact::lastUsedAtEpochMs).reversed()))
        .limit(Math.max(0, limit))
        .toList();
  }

  public Optional<TpaContact> mostContacted(@NonNull UUID ownerId) {
    var top = top(ownerId, 1);
    if (top.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(top.getFirst());
  }

  public Optional<TpaContact> lastContacted(@NonNull UUID ownerId) {
    var ownerContacts = this.contacts.getOrDefault(ownerId, Map.of());
    return ownerContacts.values().stream()
        .max(Comparator.comparingLong(TpaContact::lastUsedAtEpochMs));
  }

  private void save(@NonNull TpaContact contact) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit("save-tpa-contact", () -> this.repository.save(contact));
  }
}
