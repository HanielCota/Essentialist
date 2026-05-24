package com.hanielcota.essentials.modules.homes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.repository.HomeRepository;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class HomeServiceTest {

  private static Player player(UUID owner) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> owner;
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "HomeServicePlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  private static Location location() {
    var world =
        (World)
            Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[] {World.class},
                (ignored, method, args) -> {
                  if (method.getName().equals("getName")) {
                    return "world";
                  }
                  if (method.getName().equals("toString")) {
                    return "World";
                  }
                  throw new UnsupportedOperationException(method.getName());
                });

    return new Location(world, 1, 2, 3);
  }

  @Test
  void renameReportsNotFoundWhenSourceIsMissing() {
    var repository = new RefusingRenameRepository(false);
    var service = new HomeService(repository, new HomeLimitResolver(() -> 1));

    var result = service.rename(UUID.randomUUID(), "base", "main");

    assertEquals(HomeService.RenameResult.NOT_FOUND, result);
  }

  @Test
  void renameReportsNameTakenWhenSourceExistsButRepositoryRefuses() {
    var owner = UUID.randomUUID();
    var home = new Home(owner, "base", "world", 0, 64, 0, 0, 0, Material.RED_BED, 1);
    var repository = new RefusingRenameRepository(true);
    repository.home = home;
    var service = new HomeService(repository, new HomeLimitResolver(() -> 1));

    var result = service.rename(owner, "base", "main");

    assertEquals(HomeService.RenameResult.NAME_TAKEN, result);
  }

  @Test
  void saveNormalizesNullMaterialToDefaultIcon() {
    var owner = UUID.randomUUID();
    var repository = new RecordingRepository();
    var service = new HomeService(repository, new HomeLimitResolver(() -> 3));

    service.save(player(owner), "base", location(), null);

    assertEquals(Material.RED_BED, repository.saved.material());
  }

  @Test
  void setMaterialRejectsNonRenderableIconBeforeRepositoryMutation() {
    var repository = new RecordingRepository();
    var service = new HomeService(repository, new HomeLimitResolver(() -> 3));

    assertFalse(service.setMaterial(UUID.randomUUID(), "base", Material.AIR));
    assertEquals(0, repository.updateMaterialCalls);
  }

  private static final class RefusingRenameRepository implements HomeRepository {

    private final boolean sourceVisible;
    private Home home;

    private RefusingRenameRepository(boolean sourceVisible) {
      this.sourceVisible = sourceVisible;
    }

    @Override
    public Optional<Home> find(UUID owner, String name) {
      if (!sourceVisible || home == null) {
        return Optional.empty();
      }
      if (home.owner().equals(owner) && home.name().equalsIgnoreCase(name)) {
        return Optional.of(home);
      }
      return Optional.empty();
    }

    @Override
    public List<Home> list(UUID owner) {
      return home == null ? List.of() : List.of(home);
    }

    @Override
    public int count(UUID owner) {
      return home == null ? 0 : 1;
    }

    @Override
    public void save(Home home) {}

    @Override
    public boolean delete(UUID owner, String name) {
      return false;
    }

    @Override
    public boolean rename(UUID owner, String oldName, String newName) {
      return false;
    }

    @Override
    public boolean updateMaterial(UUID owner, String name, Material material) {
      return false;
    }
  }

  private static final class RecordingRepository implements HomeRepository {

    private Home saved;
    private int updateMaterialCalls;

    @Override
    public Optional<Home> find(UUID owner, String name) {
      return Optional.empty();
    }

    @Override
    public List<Home> list(UUID owner) {
      return List.of();
    }

    @Override
    public int count(UUID owner) {
      return 0;
    }

    @Override
    public void save(Home home) {
      saved = home;
    }

    @Override
    public boolean delete(UUID owner, String name) {
      return false;
    }

    @Override
    public boolean rename(UUID owner, String oldName, String newName) {
      return false;
    }

    @Override
    public boolean updateMaterial(UUID owner, String name, Material material) {
      updateMaterialCalls++;
      return true;
    }
  }
}
