package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;

/**
 * Storage contract for homes — pure CRUD against (owner, name) tuples plus a couple of in-place
 * field updates. Implementations decide where rows live (SQLite today, JSON or in-memory in tests).
 * The service layer depends on this interface so the persistence choice can change without touching
 * business rules (DIP).
 */
public interface HomeRepository {

  Optional<Home> find(UUID owner, String name);

  List<Home> list(UUID owner);

  List<Home> listAll();

  int count(UUID owner);

  void save(Home home);

  boolean delete(UUID owner, String name);

  boolean rename(UUID owner, String oldName, String newName);

  boolean updateMaterial(UUID owner, String name, Material material);
}
