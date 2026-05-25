package com.hanielcota.essentials.api;

import com.hanielcota.essentials.modules.nick.model.NickEntry;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/** Read-only access to nicknames. Available when the {@code nick} module is enabled. */
public interface NicksApi {

  /** Active nickname for {@code id}, or empty if the player has none set. */
  Optional<NickEntry> nickOf(@NonNull UUID id);

  /** Resolves a nickname (case-insensitive) back to the owning player's id. */
  Optional<UUID> idByNick(@NonNull String nickname);
}
