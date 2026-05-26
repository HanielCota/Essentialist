package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.repository.TpaBlockStore;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class TpaBlockService {

  private final @Nullable TpaBlockStore repository;
  private final AsyncDatabaseWriter writer;
  private final Map<UUID, Map<UUID, String>> blocked = new ConcurrentHashMap<>();

  public TpaBlockService(@Nullable TpaBlockStore repository, @NonNull AsyncDatabaseWriter writer) {
    this.repository = repository;
    this.writer = writer;
  }

  public void loadAll(@NonNull List<Entry> entries) {
    for (var entry : entries) {
      addLocal(entry.blockerId(), entry.blockedId(), entry.blockedName());
    }
  }

  public void block(@NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName) {
    addLocal(blockerId, blockedId, blockedName);
    save(blockerId, blockedId, blockedName);
  }

  public void unblock(@NonNull UUID blockerId, @NonNull UUID blockedId) {
    var playerBlocks = this.blocked.get(blockerId);
    if (playerBlocks != null) {
      playerBlocks.remove(blockedId);
    }
    delete(blockerId, blockedId);
  }

  public boolean isBlocked(@NonNull UUID blockerId, @NonNull UUID blockedId) {
    var playerBlocks = this.blocked.get(blockerId);

    return playerBlocks != null && playerBlocks.containsKey(blockedId);
  }

  public List<Entry> blockedBy(@NonNull UUID blockerId) {
    var playerBlocks = this.blocked.getOrDefault(blockerId, Map.of());

    return playerBlocks.entrySet().stream()
        .map(entry -> new Entry(blockerId, entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(Entry::blockedName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private void addLocal(
      @NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName) {
    var playerBlocks = this.blocked.computeIfAbsent(blockerId, id -> new ConcurrentHashMap<>());
    playerBlocks.put(blockedId, blockedName);
  }

  private void save(@NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit(
        "save-tpa-block", () -> this.repository.save(blockerId, blockedId, blockedName));
  }

  private void delete(@NonNull UUID blockerId, @NonNull UUID blockedId) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit("delete-tpa-block", () -> this.repository.delete(blockerId, blockedId));
  }

  public record Entry(
      @NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName) {}
}
