package com.hanielcota.essentials.modules.tpa.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Type-safe identity of a single teleport request.
 *
 * <p>Distinct from a player {@link UUID}: the compiler now rejects passing a requester or target id
 * where a request id is expected, and vice versa.
 */
public record RequestId(UUID value) {

  public RequestId {
    Objects.requireNonNull(value, "value");
  }

  /** Generates a fresh, unique request id. */
  public static RequestId random() {
    return new RequestId(UUID.randomUUID());
  }
}
