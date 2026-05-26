package com.hanielcota.essentials.modules.tpa.domain;

import lombok.NonNull;

public record TpaProfile(
    boolean receiveTpa, boolean receiveTpaHere, long sentRequests, long receivedRequests) {

  public static TpaProfile defaults() {
    return new TpaProfile(true, true, 0, 0);
  }

  public boolean accepts(@NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA -> this.receiveTpa;
      case TPAHERE -> this.receiveTpaHere;
    };
  }

  public TpaProfile toggled(@NonNull TeleportRequestType type) {
    return switch (type) {
      case TPA ->
          new TpaProfile(
              !this.receiveTpa, this.receiveTpaHere, this.sentRequests, this.receivedRequests);
      case TPAHERE ->
          new TpaProfile(
              this.receiveTpa, !this.receiveTpaHere, this.sentRequests, this.receivedRequests);
    };
  }

  public TpaProfile incrementSentRequests() {
    var nextSentRequests = this.sentRequests + 1;

    return new TpaProfile(
        this.receiveTpa, this.receiveTpaHere, nextSentRequests, this.receivedRequests);
  }

  public TpaProfile incrementReceivedRequests() {
    var nextReceivedRequests = this.receivedRequests + 1;

    return new TpaProfile(
        this.receiveTpa, this.receiveTpaHere, this.sentRequests, nextReceivedRequests);
  }
}
