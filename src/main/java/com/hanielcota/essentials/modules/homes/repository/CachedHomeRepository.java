package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * Runtime cache for homes.
 *
 * <p>The database is loaded once at module startup; command/menu reads are served from memory and
 * mutations are enqueued to the writer thread after the cache changes.
 */
@RequiredArgsConstructor
public final class CachedHomeRepository implements HomeRepository, AutoCloseable {

  private final HomeRepository delegate;
  private final AsyncDatabaseWriter writer;
  private final HomeCache cache;

  @Override
  public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    return cache.find(owner, name);
  }

  @Override
  public List<Home> list(@NonNull UUID owner) {
    return cache.list(owner);
  }

  @Override
  public List<Home> listAll() {
    return cache.listAll();
  }

  @Override
  public int count(@NonNull UUID owner) {
    return cache.count(owner);
  }

  @Override
  public void save(@NonNull Home home) {
    cache.save(home);
    writer.submit("save home", () -> delegate.save(home));
  }

  @Override
  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    var removed = cache.delete(owner, name);

    if (removed.isEmpty()) {
      return false;
    }

    var actualHome = removed.get();
    writer.submit("delete home", () -> delegate.delete(owner, actualHome.name()));

    return true;
  }

  @Override
  public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    var renamed = cache.rename(owner, oldName, newName);

    if (renamed.isEmpty()) {
      return false;
    }

    writer.submit("rename home", () -> delegate.rename(owner, oldName, newName));

    return true;
  }

  @Override
  public boolean updateMaterial(
      @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
    var updated = cache.updateMaterial(owner, name, material);

    if (updated.isEmpty()) {
      return false;
    }

    writer.submit("update home material", () -> delegate.updateMaterial(owner, name, material));

    return true;
  }

  @Override
  public void close() {
    writer.close();
  }
}
