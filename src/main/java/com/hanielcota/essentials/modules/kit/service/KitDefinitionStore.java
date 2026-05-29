package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.config.YamlReadWriter;
import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.config.KitDefinitionsConfig;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Owns {@code kits.yml}: loads the kit definitions and rewrites the file on create/delete. The
 * in-memory snapshot is updated synchronously so reads are immediately consistent; the disk write
 * is handed to an executor off the main thread.
 */
@RequiredArgsConstructor
public final class KitDefinitionStore {

  private final Path file;
  private final Executor ioExecutor;

  private volatile KitDefinitionsConfig current = KitDefinitionsConfig.defaults();

  /** Reads the definitions from disk (merging defaults). Blocks the calling thread. */
  public void load() {
    this.current =
        YamlReadWriter.readMerging(
            this.file, KitDefinitionsConfig.class, KitDefinitionsConfig::defaults);
  }

  public Map<String, KitDefinitionConfig> all() {
    return this.current.kits();
  }

  public void put(@NonNull String id, @NonNull KitDefinitionConfig definition) {
    var updated = new LinkedHashMap<>(this.current.kits());
    updated.put(id, definition);

    persist(updated);
  }

  public boolean remove(@NonNull String id) {
    var updated = new LinkedHashMap<>(this.current.kits());
    var removed = updated.remove(id) != null;

    if (removed) {
      persist(updated);
    }

    return removed;
  }

  private void persist(@NonNull Map<String, KitDefinitionConfig> kits) {
    var snapshot = new KitDefinitionsConfig(kits);
    this.current = snapshot;

    var target = this.file;
    this.ioExecutor.execute(
        () -> YamlReadWriter.write(target, KitDefinitionsConfig.class, snapshot));
  }
}
