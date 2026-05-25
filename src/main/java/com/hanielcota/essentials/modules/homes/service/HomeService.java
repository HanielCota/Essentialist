package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.material.HomeMaterials;
import com.hanielcota.essentials.modules.homes.repository.HomeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

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

  public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    return this.repository.find(owner, name);
  }

  public List<Home> list(@NonNull UUID owner) {
    return this.repository.list(owner);
  }

  public int count(@NonNull UUID owner) {
    return this.repository.count(owner);
  }

  public int limit(@NonNull Player owner) {
    return this.limits.resolve(owner);
  }

  public SaveResult save(
      @NonNull Player owner,
      @NonNull String name,
      @NonNull Location location,
      @Nullable Material material) {
    var ownerId = owner.getUniqueId();
    var existing = this.repository.find(ownerId, name);
    var sanitizedMaterial = HomeMaterials.sanitizeIcon(material);

    if (existing.isPresent()) {
      var previous = existing.get();
      var previousMaterial = previous.material();
      var keepMaterial = material != null ? sanitizedMaterial : previousMaterial;

      var updatedHome = Home.of(ownerId, name, location, keepMaterial);
      this.repository.save(updatedHome);

      return SaveResult.UPDATED;
    }

    var currentCount = this.repository.count(ownerId);
    var maxAllowed = this.limits.resolve(owner);

    if (currentCount >= maxAllowed) {
      return SaveResult.LIMIT_REACHED;
    }

    var newHome = Home.of(ownerId, name, location, sanitizedMaterial);
    this.repository.save(newHome);

    return SaveResult.CREATED;
  }

  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    return this.repository.delete(owner, name);
  }

  public RenameResult rename(
      @NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    // Try the atomic rename first — the bucket's own check is the source of
    // truth. Pre-checking with two finds risked reporting NAME_TAKEN when the
    // cache was actually empty (or NOT_FOUND when it actually had the entry)
    // if eviction interleaved between the finds and the rename.
    var renamed = this.repository.rename(owner, oldName, newName);

    if (renamed) {
      return RenameResult.RENAMED;
    }

    var existing = this.repository.find(owner, oldName);

    if (existing.isEmpty()) {
      return RenameResult.NOT_FOUND;
    }

    return RenameResult.NAME_TAKEN;
  }

  public boolean setMaterial(
      @NonNull UUID owner, @NonNull String name, @Nullable Material material) {
    if (!HomeMaterials.isUsableIcon(material)) {
      return false;
    }

    return this.repository.updateMaterial(owner, name, material);
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
