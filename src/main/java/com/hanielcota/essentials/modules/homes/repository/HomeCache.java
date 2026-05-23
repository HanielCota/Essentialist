package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.Material;

public final class HomeCache {

  private final ConcurrentHashMap<UUID, HomeBucket> homes = new ConcurrentHashMap<>();

  public HomeCache(@NonNull Collection<Home> homes) {
    homes.forEach(this::save);
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

  List<Home> listAll() {
    var homeComparator = Comparator.comparing(Home::name);

    return this.homes.values().stream()
        .flatMap(bucket -> bucket.list().stream())
        .sorted(homeComparator)
        .toList();
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
