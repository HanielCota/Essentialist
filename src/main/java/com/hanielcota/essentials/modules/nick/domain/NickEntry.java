package com.hanielcota.essentials.modules.nick.domain;

import java.util.UUID;
import lombok.NonNull;

/** Stored nickname assignment. {@code realName} is the player's name at the time of /nick. */
public record NickEntry(@NonNull UUID id, @NonNull String nickname, @NonNull String realName) {}
