package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Material;

/**
 * Storage contract for homes — pure CRUD against (owner, name) tuples plus a couple of in-place
 * field updates. Implementations decide where rows live (SQLite today, JSON or in-memory in tests).
 * The service layer depends on this interface so the persistence choice can change without touching
 * business rules (DIP).
 */
public interface HomeRepository {

  Optional<Home> find(@NonNull UUID owner, @NonNull String name);

  List<Home> list(@NonNull UUID owner);

  int count(@NonNull UUID owner);

  void save(@NonNull Home home);

  boolean delete(@NonNull UUID owner, @NonNull String name);

  boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName);

  boolean updateMaterial(@NonNull UUID owner, @NonNull String name, @NonNull Material material);

  boolean updatePinned(@NonNull UUID owner, @NonNull String name, boolean pinned);
}
