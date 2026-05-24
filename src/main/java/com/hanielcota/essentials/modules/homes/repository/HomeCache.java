package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.Material;

public final class HomeCache {

  private final ConcurrentHashMap<UUID, HomeBucket> homes = new ConcurrentHashMap<>();

  /** Replaces (or creates) the bucket for {@code owner} with {@code homes}, atomically. */
  void loadFor(@NonNull UUID owner, @NonNull Collection<Home> homes) {
    var bucket = new HomeBucket();
    homes.forEach(bucket::save);
    this.homes.put(owner, bucket);
  }

  /** Drops {@code owner}'s bucket from memory. SQL is untouched. */
  void evictFor(@NonNull UUID owner) {
    this.homes.remove(owner);
  }

  Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return Optional.empty();
    }

    return bucket.find(name);
  }

  List<Home> list(@NonNull UUID owner) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return List.of();
    }

    return bucket.list();
  }

  int count(@NonNull UUID owner) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return 0;
    }

    return bucket.count();
  }

  void save(@NonNull Home home) {
    var ownerId = home.owner();
    bucket(ownerId).save(home);
  }

  Optional<Home> delete(@NonNull UUID owner, @NonNull String name) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return Optional.empty();
    }

    return bucket.delete(name);
  }

  Optional<Home> rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return Optional.empty();
    }

    return bucket.rename(oldName, newName);
  }

  Optional<Home> updateMaterial(
      @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
    var bucket = this.homes.get(owner);
    if (bucket == null) {
      return Optional.empty();
    }

    return bucket.updateMaterial(name, material);
  }

  private HomeBucket bucket(@NonNull UUID owner) {
    return this.homes.computeIfAbsent(owner, ignored -> new HomeBucket());
  }
}
