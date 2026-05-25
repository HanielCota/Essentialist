package com.hanielcota.essentials.api;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/** Read-only access to mutes. Available when the {@code mute} module is enabled. */
public interface MutesApi {

  /** Whether the player currently has an active mute (timed not yet expired, or permanent). */
  boolean isMuted(@NonNull UUID id);

  /** The active mute for {@code id}, or empty if the player is not muted. */
  Optional<Mute> activeMute(@NonNull UUID id);
}
