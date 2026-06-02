package com.hanielcota.essentials.modules.invsee.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class InvseeHolderTest {

  @Test
  void constructsWithTargetId() {
    var ownerId = UUID.randomUUID();
    var holder = new InvseeHolder(ownerId);

    assertEquals(ownerId, holder.targetId());
  }
}
