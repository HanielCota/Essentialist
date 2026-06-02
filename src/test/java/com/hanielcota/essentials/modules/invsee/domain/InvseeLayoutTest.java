package com.hanielcota.essentials.modules.invsee.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvseeLayoutTest {

  @Test
  void sizeIs45() {
    assertEquals(45, InvseeLayout.SIZE);
  }

  @Test
  void storageSlotsIs36() {
    assertEquals(36, InvseeLayout.STORAGE_SLOTS);
  }

  @Test
  void armorSlotsHaveExpectedIndices() {
    assertEquals(36, InvseeLayout.HELMET_SLOT);
    assertEquals(37, InvseeLayout.CHESTPLATE_SLOT);
    assertEquals(38, InvseeLayout.LEGGINGS_SLOT);
    assertEquals(39, InvseeLayout.BOOTS_SLOT);
  }

  @Test
  void offhandSlotIs40() {
    assertEquals(40, InvseeLayout.OFFHAND_SLOT);
  }

  @Test
  void firstLockedSlotIs41() {
    assertEquals(41, InvseeLayout.FIRST_LOCKED_SLOT);
  }
}
