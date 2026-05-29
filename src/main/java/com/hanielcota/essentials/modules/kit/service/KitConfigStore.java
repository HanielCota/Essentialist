package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.YamlReadWriter;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Owns the single {@code kit.yml}: it is both the read handle for the static config (categories,
 * menus, messages, behaviour) and the writer for the kit definitions section, which {@code /kit
 * create}, {@code /kit delete} and {@code /kit set*} rewrite.
 *
 * <p>Because the module writes this file, it cannot also be loaded through the shared config
 * service (two owners would diverge), so the store implements {@link ConfigHandle} itself. Writes
 * are synchronous: the file is small and only touched by (rare) admin commands, and a synchronous
 * write keeps {@link #load()} from racing a not-yet-flushed write on an immediate {@code /kit
 * reload}.
 */
@RequiredArgsConstructor
public final class KitConfigStore implements ConfigHandle<KitConfig> {

  private final Path file;

  private volatile KitConfig current = KitConfig.defaults();

  @Override
  public String name() {
    return "kit";
  }

  @Override
  public KitConfig value() {
    return this.current;
  }

  /** Reads {@code kit.yml} from disk (merging defaults). Blocks the calling thread. */
  public void load() {
    this.current = YamlReadWriter.readMerging(this.file, KitConfig.class, KitConfig::defaults);
  }

  public Map<String, KitDefinitionConfig> kits() {
    return this.current.kits();
  }

  public void putKit(@NonNull String id, @NonNull KitDefinitionConfig definition) {
    var updated = new LinkedHashMap<>(this.current.kits());
    updated.put(id, definition);

    persist(updated);
  }

  public boolean removeKit(@NonNull String id) {
    var updated = new LinkedHashMap<>(this.current.kits());
    var removed = updated.remove(id) != null;

    if (removed) {
      persist(updated);
    }

    return removed;
  }

  private void persist(@NonNull Map<String, KitDefinitionConfig> kits) {
    var snapshot = this.current.withKits(kits);
    this.current = snapshot;

    YamlReadWriter.write(this.file, KitConfig.class, snapshot);
  }
}
