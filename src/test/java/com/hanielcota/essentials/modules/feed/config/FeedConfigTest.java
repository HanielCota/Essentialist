package com.hanielcota.essentials.modules.feed.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FeedConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = FeedConfig.defaults();

    assertTrue(!snap.fed().isEmpty());
    assertTrue(!snap.fedOther().isEmpty());
    assertTrue(!snap.alreadyFull().isEmpty());
    assertTrue(!snap.alreadyFullOther().isEmpty());
    assertTrue(!snap.fedAll().isEmpty());
  }

  @Test
  void whenFedReturnsPair() {
    var snap = FeedConfig.defaults();
    var pair = snap.whenFed();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }

  @Test
  void whenAlreadyFullReturnsPair() {
    var snap = FeedConfig.defaults();
    var pair = snap.whenAlreadyFull();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }

  @Test
  void formatFedAllReplacesCount() {
    var snap = FeedConfig.defaults();
    var msg = snap.formatFedAll(5);

    assertTrue(msg.contains("5"));
  }
}
