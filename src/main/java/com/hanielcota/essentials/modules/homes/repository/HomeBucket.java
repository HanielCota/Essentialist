package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Material;

final class HomeBucket {

  private final Map<String, Home> homes = new HashMap<>();

  private static List<Home> sorted(Collection<Home> homes) {
    var list = new ArrayList<>(homes);
    list.sort(Comparator.comparing(Home::name));
    return List.copyOf(list);
  }

  private static String key(String name) {
    return name.toLowerCase(Locale.ROOT);
  }

  synchronized Optional<Home> find(@NonNull String name) {
    var homeKey = key(name);
    return Optional.ofNullable(homes.get(homeKey));
  }

  synchronized List<Home> list() {
    var values = homes.values();
    return sorted(values);
  }

  synchronized int count() {
    return homes.size();
  }

  synchronized void save(@NonNull Home home) {
    var homeKey = key(home.name());
    homes.put(homeKey, home);
  }

  synchronized Optional<Home> delete(@NonNull String name) {
    var homeKey = key(name);
    var removed = homes.remove(homeKey);
    return Optional.ofNullable(removed);
  }

  synchronized Optional<Home> rename(@NonNull String oldName, @NonNull String newName) {
    var oldKey = key(oldName);
    var newKey = key(newName);
    var home = homes.get(oldKey);

    if (home == null || homes.containsKey(newKey)) {
      return Optional.empty();
    }

    homes.remove(oldKey);

    var renamed = home.withName(newName);
    homes.put(newKey, renamed);

    return Optional.of(renamed);
  }

  synchronized Optional<Home> updateMaterial(@NonNull String name, @NonNull Material material) {
    var homeKey = key(name);
    var home = homes.get(homeKey);

    if (home == null) {
      return Optional.empty();
    }

    var updated = home.withMaterial(material);
    homes.put(homeKey, updated);

    return Optional.of(updated);
  }
}
