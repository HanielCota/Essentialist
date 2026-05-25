package com.hanielcota.essentials.api;

import java.util.Set;
import java.util.UUID;
import lombok.NonNull;

/** Read-only access to vanish state. Available when the {@code vanish} module is enabled. */
public interface VanishApi {

  /** Whether {@code id} is currently vanished. */
  boolean isVanished(@NonNull UUID id);

  /** Snapshot of currently vanished player ids. */
  Set<UUID> vanished();
}
