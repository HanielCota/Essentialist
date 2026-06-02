package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ChatPermissionsTest {

  @Test
  void adminPermissionIsDefined() {
    assertNotNull(ChatPermissions.ADMIN);
    assertEquals("chat.admin", ChatPermissions.ADMIN);
  }

  @Test
  void allChannelUsePermissionsAreNonEmpty() {
    assertNotNull(ChatPermissions.GLOBAL_USE);
    assertNotNull(ChatPermissions.STAFF_USE);
  }

  @Test
  void bypassPermissionsAreNonEmpty() {
    assertNotNull(ChatPermissions.BYPASS_ANTISPAM);
    assertNotNull(ChatPermissions.GLOBAL_BYPASS_COOLDOWN);
    assertNotNull(ChatPermissions.LOCAL_BYPASS_COOLDOWN);
    assertNotNull(ChatPermissions.LOCAL_BYPASS_RANGE);
    assertNotNull(ChatPermissions.STAFF_BYPASS_COOLDOWN);
  }

  @Test
  void stylePermissionsAreNonEmpty() {
    assertNotNull(ChatPermissions.CHAT_COLOR);
    assertNotNull(ChatPermissions.CHAT_FORMAT);
  }

  @Test
  void receivePermissionIsNonEmpty() {
    assertNotNull(ChatPermissions.STAFF_RECEIVE);
  }
}
