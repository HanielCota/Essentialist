package com.hanielcota.essentials.modules.environment.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class EnvironmentConfigTest {

  @Test
  void defaultsProtectEveryWorld() {
    var snap = EnvironmentConfig.defaults();

    assertTrue(snap.enabled());
    assertTrue(snap.appliesTo("world"));
    assertTrue(snap.appliesTo("world_nether"));
  }

  @Test
  void emptyListsBlockEverySubject() {
    var snap = EnvironmentConfig.defaults();

    assertTrue(snap.isIgniteCauseBlocked("LIGHTNING"));
    assertTrue(snap.isExplosionSourceBlocked("CREEPER"));
    assertTrue(snap.isGriefEntityBlocked("ENDERMAN"));
  }

  @Test
  void nonEmptyListsBlockOnlyListedSubjects() {
    var snap =
        new EnvironmentConfig(
            true,
            EnvironmentConfig.WorldMode.BLACKLIST,
            List.of(),
            "",
            true,
            true,
            true,
            List.of("LIGHTNING"),
            true,
            List.of("CREEPER"),
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            true,
            List.of("ENDERMAN"),
            false);

    assertTrue(snap.isIgniteCauseBlocked("LIGHTNING"));
    assertFalse(snap.isIgniteCauseBlocked("FLINT_AND_STEEL"));
    assertTrue(snap.isExplosionSourceBlocked("CREEPER"));
    assertFalse(snap.isExplosionSourceBlocked("PRIMED_TNT"));
    assertTrue(snap.isGriefEntityBlocked("ENDERMAN"));
    assertFalse(snap.isGriefEntityBlocked("ZOMBIE"));
  }
}
