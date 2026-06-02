package com.hanielcota.essentials.modules.skull.domain;

public record SkullDelivery(boolean inventoryFull) {

  public boolean isSuccess() {
    return !inventoryFull;
  }
}
