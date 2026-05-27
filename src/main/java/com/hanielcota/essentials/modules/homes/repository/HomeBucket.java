package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.Material;

final class HomeBucket {

  private final Map<String, Home> homes = new ConcurrentHashMap<>();

  private static final Comparator<Home> BY_PINNED_THEN_NAME =
      Comparator.comparing(Home::pinned).reversed().thenComparing(Home::name);

  private static List<Home> sorted(@NonNull Collection<Home> homes) {
    var list = new ArrayList<>(homes);
    list.sort(BY_PINNED_THEN_NAME);
    return List.copyOf(list);
  }

  private static String key(@NonNull String name) {
    return name.toLowerCase(Locale.ROOT);
  }

  Optional<Home> find(@NonNull String name) {
    var homeKey = key(name);
    var home = this.homes.get(homeKey);

    return Optional.ofNullable(home);
  }

  List<Home> list() {
    var values = this.homes.values();
    return sorted(values);
  }

  int count() {
    return this.homes.size();
  }

  void save(@NonNull Home home) {
    var homeName = home.name();
    var homeKey = key(homeName);

    this.homes.put(homeKey, home);
  }

  Optional<Home> delete(@NonNull String name) {
    var homeKey = key(name);
    var removed = this.homes.remove(homeKey);

    return Optional.ofNullable(removed);
  }

  // putIfAbsent first guarantees we never collide with a concurrent rename onto the new key; if
  // that succeeds we then atomically drop the old entry. A failed putIfAbsent leaves both keys
  // intact (rename rejected because the new name already exists).
  Optional<Home> rename(@NonNull String oldName, @NonNull String newName) {
    var oldKey = key(oldName);
    var newKey = key(newName);

    var existing = this.homes.get(oldKey);
    if (existing == null) {
      return Optional.empty();
    }

    var renamed = existing.withName(newName);
    var conflict = this.homes.putIfAbsent(newKey, renamed);
    if (conflict != null) {
      return Optional.empty();
    }

    this.homes.remove(oldKey, existing);
    return Optional.of(renamed);
  }

  Optional<Home> updateMaterial(@NonNull String name, @NonNull Material material) {
    var homeKey = key(name);

    var updated =
        this.homes.computeIfPresent(homeKey, (k, current) -> current.withMaterial(material));
    return Optional.ofNullable(updated);
  }

  Optional<Home> updatePinned(@NonNull String name, boolean pinned) {
    var homeKey = key(name);

    var updated = this.homes.computeIfPresent(homeKey, (k, current) -> current.withPinned(pinned));
    return Optional.ofNullable(updated);
  }

  Optional<Home> bumpUsage(@NonNull String name, long timestampMs) {
    var homeKey = key(name);

    var updated =
        this.homes.computeIfPresent(homeKey, (k, current) -> current.withUsageBump(timestampMs));
    return Optional.ofNullable(updated);
  }
}
