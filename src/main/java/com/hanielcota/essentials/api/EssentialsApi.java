package com.hanielcota.essentials.api;

import java.util.Optional;

/**
 * Public entry point for addons that depend on Essentialist.
 *
 * <p>Domain-specific facades are returned as {@link Optional} so addons gracefully degrade when a
 * module is disabled — e.g. an addon that wants to react to vanish state should bail when {@code
 * vanish()} is empty instead of throwing.
 */
public interface EssentialsApi {

  Optional<HomesApi> homes();

  Optional<WarpsApi> warps();

  Optional<MutesApi> mutes();

  Optional<NicksApi> nicks();

  Optional<VanishApi> vanish();

  Optional<TeleportsApi> teleports();
}
