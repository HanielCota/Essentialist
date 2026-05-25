package com.hanielcota.essentials.api;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/** Read-only access to per-player homes. Available when the {@code homes} module is enabled. */
public interface HomesApi {

  /** All homes owned by {@code owner}, sorted by name. Empty list if the player has none. */
  List<Home> homesOf(@NonNull UUID owner);

  /** Specific home by name (case-insensitive), or empty if not found. */
  Optional<Home> findHome(@NonNull UUID owner, @NonNull String name);

  /** Number of homes {@code owner} currently has. */
  int homeCount(@NonNull UUID owner);
}
