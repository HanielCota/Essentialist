package com.hanielcota.essentials.module.control;

import com.hanielcota.essentials.config.YamlReadWriter;
import com.hanielcota.essentials.module.discovery.ModuleSettings;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

/**
 * Read/write access to the module enable switches in {@code modules.yml}, plus the running
 * (boot-time) state captured at discovery.
 *
 * <p>Modules are discovered and enabled once at boot, and commands cannot be unregistered at
 * runtime, so a toggle is persisted immediately but only takes effect on the next restart. {@link
 * #pendingRestart(String)} surfaces that gap (persisted flag differs from the running state).
 *
 * <p>The persisted flags are mirrored in memory (seeded from the boot settings) so reads do not hit
 * disk; only {@link #setEnabled} writes. Single-threaded use (admin menu clicks on the main
 * thread).
 */
public final class ModuleControl {

  private final Path settingsFile;
  private final List<String> moduleIds;
  private final Set<String> runningIds;
  private final Map<String, Boolean> persisted;

  public ModuleControl(
      @NonNull Path settingsFile,
      @NonNull List<String> moduleIds,
      @NonNull Set<String> runningIds,
      @NonNull Map<String, Boolean> bootSettings) {
    this.settingsFile = settingsFile;
    this.moduleIds = List.copyOf(moduleIds);
    this.runningIds = Set.copyOf(runningIds);
    this.persisted = new LinkedHashMap<>(bootSettings);
  }

  /** All discovered module ids (enabled and disabled), sorted. */
  public List<String> moduleIds() {
    return this.moduleIds;
  }

  /** Whether the module is actually enabled in this session (decided at boot). */
  public boolean runningEnabled(@NonNull String moduleId) {
    return this.runningIds.contains(moduleId);
  }

  /** The persisted switch in modules.yml — missing entries default to enabled. */
  public boolean persistedEnabled(@NonNull String moduleId) {
    return this.persisted.getOrDefault(moduleId, true);
  }

  /** True when the persisted switch no longer matches what is running — a restart is needed. */
  public boolean pendingRestart(@NonNull String moduleId) {
    return persistedEnabled(moduleId) != runningEnabled(moduleId);
  }

  /** Flips the persisted switch, writes modules.yml, and returns the new value. */
  public boolean toggle(@NonNull String moduleId) {
    var next = !persistedEnabled(moduleId);
    setEnabled(moduleId, next);

    return next;
  }

  public void setEnabled(@NonNull String moduleId, boolean enabled) {
    this.persisted.put(moduleId, enabled);

    var snapshot = new ModuleSettings(this.persisted);
    YamlReadWriter.write(this.settingsFile, ModuleSettings.class, snapshot);
  }
}
