package com.hanielcota.essentials.modules.homes.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Application service for the homes use cases.
 *
 * <p>Sole responsibility: enforce the per-player limit and delegate persistence to a {@link
 * HomeRepository}. Owns no state of its own; reads ({@link #find}, {@link #list}) hit the
 * repository directly.
 */
@RequiredArgsConstructor
public final class HomeService {

  private final HomeRepository repository;
  private final HomeLimitResolver limits;

  public Optional<Home> find(UUID owner, String name) {
    return repository.find(owner, name);
  }

  public List<Home> list(UUID owner) {
    return repository.list(owner);
  }

  public int count(UUID owner) {
    return repository.count(owner);
  }

  public int limit(Player owner) {
    return limits.resolve(owner);
  }

  public SaveResult save(Player owner, String name, Location location, Material material) {
    var ownerId = owner.getUniqueId();
    var existing = repository.find(ownerId, name);

    if (existing.isPresent()) {
      var keepMaterial = material != null ? material : existing.get().material();
      repository.save(Home.of(ownerId, name, location, keepMaterial));
      return SaveResult.UPDATED;
    }
    if (repository.count(ownerId) >= limits.resolve(owner)) {
      return SaveResult.LIMIT_REACHED;
    }

    repository.save(Home.of(ownerId, name, location, material));
    return SaveResult.CREATED;
  }

  public boolean delete(UUID owner, String name) {
    return repository.delete(owner, name);
  }

  public RenameResult rename(UUID owner, String oldName, String newName) {
    if (repository.find(owner, oldName).isEmpty()) {
      return RenameResult.NOT_FOUND;
    }
    if (repository.find(owner, newName).isPresent()) {
      return RenameResult.NAME_TAKEN;
    }

    repository.rename(owner, oldName, newName);
    return RenameResult.RENAMED;
  }

  public boolean setMaterial(UUID owner, String name, Material material) {
    return repository.updateMaterial(owner, name, material);
  }

  public enum SaveResult {
    CREATED,
    UPDATED,
    LIMIT_REACHED
  }

  public enum RenameResult {
    RENAMED,
    NOT_FOUND,
    NAME_TAKEN
  }
}
