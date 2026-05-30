package com.hanielcota.essentials.modules.ban.domain;

import java.util.UUID;
import lombok.NonNull;

/**
 * A ban paired with the identity of the banned account. Carried by the cache and the ban-list menu
 * so the player name is available without a second lookup ({@link Ban} alone holds only the
 * sentence, not who it applies to).
 */
public record ActiveBan(@NonNull UUID id, @NonNull String name, @NonNull Ban ban) {}
