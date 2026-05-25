package com.hanielcota.essentials.modules.seen.domain;

import lombok.NonNull;

/**
 * Domain description of a {@code /seen} lookup result. The kind drives which message template
 * (online / offline) is rendered by the command; never-seen is signalled by {@link
 * SeenService#describe} returning an empty optional.
 */
public record SeenLine(@NonNull Kind kind, @NonNull String displayName, @NonNull String duration) {

  public enum Kind {
    ONLINE,
    OFFLINE
  }
}
