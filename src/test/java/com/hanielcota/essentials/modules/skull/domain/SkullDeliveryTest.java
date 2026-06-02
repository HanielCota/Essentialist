package com.hanielcota.essentials.modules.skull.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SkullDeliveryTest {

  @Test
  void isSuccessWhenInventoryNotFull() {
    var delivery = new SkullDelivery(false);

    assertTrue(delivery.isSuccess());
  }

  @Test
  void isNotSuccessWhenInventoryFull() {
    var delivery = new SkullDelivery(true);

    assertFalse(delivery.isSuccess());
  }
}
