package com.hanielcota.essentials.modules.tpa.domain;

import java.util.UUID;
import lombok.NonNull;

/** A single favorite entry: the owner picked {@code favoriteId} as a quick-TPA shortcut. */
public record TpaFavorite(
    @NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName) {}
