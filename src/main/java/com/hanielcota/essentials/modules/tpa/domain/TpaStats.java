package com.hanielcota.essentials.modules.tpa.domain;

import java.time.Duration;
import lombok.NonNull;

public record TpaStats(
    long sentRequests,
    long receivedRequests,
    long acceptedSent,
    long acceptCount,
    long totalAcceptLatencyMs) {

  public static TpaStats empty() {
    return new TpaStats(0, 0, 0, 0, 0);
  }

  public TpaStats incrementSentRequests() {
    return new TpaStats(
        this.sentRequests + 1,
        this.receivedRequests,
        this.acceptedSent,
        this.acceptCount,
        this.totalAcceptLatencyMs);
  }

  public TpaStats incrementReceivedRequests() {
    return new TpaStats(
        this.sentRequests,
        this.receivedRequests + 1,
        this.acceptedSent,
        this.acceptCount,
        this.totalAcceptLatencyMs);
  }

  public TpaStats recordAcceptedOutgoing(@NonNull Duration latency) {
    var latencyMs = Math.max(0L, latency.toMillis());

    return new TpaStats(
        this.sentRequests,
        this.receivedRequests,
        this.acceptedSent + 1,
        this.acceptCount + 1,
        this.totalAcceptLatencyMs + latencyMs);
  }
}
