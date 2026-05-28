package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.api.HomesApi;
import com.hanielcota.essentials.modules.homes.domain.CreateResult;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.domain.RenameResult;
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
 * Application service for the homes use cases. Enforces the per-player limit and delegates
 * persistence to a {@link HomeRepository}.
 */
@RequiredArgsConstructor
public final class HomeService implements HomesApi {

  private final HomeRepository repository;
  private final HomeLimitResolver limits;

  public Optional<Home> findHome(@NonNull UUID owner, @NonNull String name) {
    return this.repository.find(owner, name);
  }

  public List<Home> homesOf(@NonNull UUID owner) {
    return this.repository.list(owner);
  }

  public int homeCount(@NonNull UUID owner) {
    return this.repository.count(owner);
  }

  public int limit(@NonNull Player owner) {
    return this.limits.resolve(owner);
  }

  public CreateResult createNew(
      @NonNull Player owner,
      @NonNull String name,
      @NonNull Location location,
      @Nullable Material material) {
    var ownerId = owner.getUniqueId();
    var existing = this.repository.find(ownerId, name);

    if (existing.isPresent()) {
      return CreateResult.ALREADY_EXISTS;
    }

    var currentCount = this.repository.count(ownerId);
    var maxAllowed = this.limits.resolve(owner);

    if (currentCount >= maxAllowed) {
      return CreateResult.LIMIT_REACHED;
    }

    var sanitizedMaterial = HomeMaterials.sanitizeIcon(material);
    var newHome = Home.of(ownerId, name, location, sanitizedMaterial);
    this.repository.save(newHome);

    return CreateResult.CREATED;
  }

  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    return this.repository.delete(owner, name);
  }

  public RenameResult rename(
      @NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
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

  public boolean setPinned(@NonNull UUID owner, @NonNull String name, boolean pinned) {
    return this.repository.updatePinned(owner, name, pinned);
  }

  public boolean recordUsage(@NonNull UUID owner, @NonNull String name, long timestampMs) {
    return this.repository.bumpUsage(owner, name, timestampMs);
  }
}
