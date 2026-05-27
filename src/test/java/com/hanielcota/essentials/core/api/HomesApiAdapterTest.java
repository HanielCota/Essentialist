package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.api.HomesApi;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.repository.HomeRepository;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class HomesApiAdapterTest {

  @Test
  void homesOfReturnsRepositoryEntries() {
    var owner = UUID.randomUUID();
    var first = home(owner, "base");
    var second = home(owner, "mine");
    var api = apiWith(first, second);

    assertEquals(List.of(first, second), api.homesOf(owner));
  }

  @Test
  void findHomeFiltersByName() {
    var owner = UUID.randomUUID();
    var base = home(owner, "base");
    var api = apiWith(base, home(owner, "mine"));

    assertEquals(base, api.findHome(owner, "base").orElseThrow());
    assertTrue(api.findHome(owner, "missing").isEmpty());
  }

  @Test
  void homeCountMatchesRepositoryCount() {
    var owner = UUID.randomUUID();
    var api = apiWith(home(owner, "a"), home(owner, "b"), home(owner, "c"));

    assertEquals(3, api.homeCount(owner));
  }

  private static HomesApi apiWith(@NonNull Home... homes) {
    var repository = new InMemoryHomeRepository(List.of(homes));
    return new HomeService(repository, new HomeLimitResolver(() -> 0));
  }

  private static Home home(@NonNull UUID owner, @NonNull String name) {
    return new Home(owner, name, "world", 0, 64, 0, 0, 0, Material.RED_BED, 0L);
  }

  private static final class InMemoryHomeRepository implements HomeRepository {

    private final List<Home> rows;

    InMemoryHomeRepository(@NonNull Collection<Home> seed) {
      this.rows = new ArrayList<>(seed);
    }

    @Override
    public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
      return this.rows.stream()
          .filter(h -> h.owner().equals(owner) && h.name().equalsIgnoreCase(name))
          .findFirst();
    }

    @Override
    public List<Home> list(@NonNull UUID owner) {
      return this.rows.stream().filter(h -> h.owner().equals(owner)).collect(Collectors.toList());
    }

    @Override
    public int count(@NonNull UUID owner) {
      return list(owner).size();
    }

    @Override
    public void save(@NonNull Home home) {
      this.rows.add(home);
    }

    @Override
    public boolean delete(@NonNull UUID owner, @NonNull String name) {
      return this.rows.removeIf(h -> h.owner().equals(owner) && h.name().equalsIgnoreCase(name));
    }

    @Override
    public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
      return false;
    }

    @Override
    public boolean updateMaterial(
        @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
      return false;
    }
  }
}
