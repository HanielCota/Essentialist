package com.hanielcota.essentials.modules.msg.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MsgConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = MsgConfig.defaults();

    assertTrue(!snap.outgoingFormat().isEmpty());
    assertTrue(!snap.incomingFormat().isEmpty());
    assertTrue(!snap.emptyMessage().isEmpty());
    assertTrue(!snap.cannotMessageSelf().isEmpty());
    assertTrue(!snap.targetUnavailable().isEmpty());
    assertTrue(!snap.noReplyPartner().isEmpty());
    assertTrue(!snap.replyPartnerUnavailable().isEmpty());
  }

  @Test
  void outgoingFormatContainsTargetPlaceholder() {
    var snap = MsgConfig.defaults();

    assertTrue(snap.outgoingFormat().contains("{target}"));
    assertTrue(snap.outgoingFormat().contains("{message}"));
  }

  @Test
  void incomingFormatContainsSenderPlaceholder() {
    var snap = MsgConfig.defaults();

    assertTrue(snap.incomingFormat().contains("{sender}"));
    assertTrue(snap.incomingFormat().contains("{message}"));
  }
}
