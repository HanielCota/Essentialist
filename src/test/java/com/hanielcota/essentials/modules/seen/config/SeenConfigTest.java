package com.hanielcota.essentials.modules.seen.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.seen.domain.SeenLine;
import org.junit.jupiter.api.Test;

class SeenConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = SeenConfig.defaults();

    assertTrue(!snap.online().isEmpty());
    assertTrue(!snap.offline().isEmpty());
    assertTrue(!snap.neverSeen().isEmpty());
  }

  @Test
  void formatOnlineReplacesPlaceholders() {
    var snap = SeenConfig.defaults();
    var msg = snap.formatOnline("Steve", "5 minutes");

    assertTrue(msg.contains("Steve"));
    assertTrue(msg.contains("5 minutes"));
  }

  @Test
  void formatOfflineReplacesPlaceholders() {
    var snap = SeenConfig.defaults();
    var msg = snap.formatOffline("Alex", "2 hours");

    assertTrue(msg.contains("Alex"));
    assertTrue(msg.contains("2 hours"));
  }

  @Test
  void formatNeverSeenReplacesPlayer() {
    var snap = SeenConfig.defaults();
    var msg = snap.formatNeverSeen("Ghost");

    assertTrue(msg.contains("Ghost"));
  }

  @Test
  void formatLineRoutesToOnlineForOnlineKind() {
    var snap = SeenConfig.defaults();
    var line = new SeenLine(SeenLine.Kind.ONLINE, "Steve", "1 minute");
    var msg = snap.formatLine(line);

    assertTrue(msg.contains("Steve"));
    assertTrue(msg.contains("now"));
  }

  @Test
  void formatLineRoutesToOfflineForOfflineKind() {
    var snap = SeenConfig.defaults();
    var line = new SeenLine(SeenLine.Kind.OFFLINE, "Alex", "1 hour");
    var msg = snap.formatLine(line);

    assertTrue(msg.contains("Alex"));
    assertTrue(msg.contains("last seen"));
  }
}
