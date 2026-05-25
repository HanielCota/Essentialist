package com.hanielcota.essentials.api;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;
import java.util.Optional;

/**
 * Public entry point for addons that depend on Essentialist.
 *
 * <p>Domain-specific facades are returned as {@link Optional} so addons gracefully degrade when a
 * module is disabled — e.g. an addon that wants to react to vanish state should bail when {@code
 * vanish()} is empty instead of throwing.
 *
 * <p>{@link #plugin()} and {@link #services()} are kept for backwards-compatibility and internal
 * tooling; they are marked {@link Internal} and may be removed once the typed accessors cover every
 * public use case.
 */
public interface EssentialsApi {

  Optional<HomesApi> homes();

  Optional<WarpsApi> warps();

  Optional<MutesApi> mutes();

  Optional<NicksApi> nicks();

  Optional<VanishApi> vanish();

  Optional<TeleportsApi> teleports();

  /**
   * The owning {@link EssentialsPlugin}. Plugin-internal — addons should not depend on this; it
   * exposes Bukkit lifecycle that breaks if Essentialist swaps platform abstractions.
   *
   * @deprecated use the domain-specific accessors above.
   */
  @Internal
  @Deprecated
  EssentialsPlugin plugin();

  /**
   * The raw service registry. Plugin-internal — addons should not depend on this; service types and
   * registration timing can change between releases without notice.
   *
   * @deprecated use the domain-specific accessors above.
   */
  @Internal
  @Deprecated
  ServiceRegistry services();
}
