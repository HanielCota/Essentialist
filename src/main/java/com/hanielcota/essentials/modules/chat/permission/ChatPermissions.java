package com.hanielcota.essentials.modules.chat.permission;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Permission keys recognised by the {@code chat} module. Kept in one place so a rename touches one
 * file instead of every command/listener/channel.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatPermissions {

  public static final String ADMIN = "chat.admin";
  public static final String RELOAD = "chat.reload";

  public static final String GLOBAL_USE = "chat.global.use";

  public static final String LOCAL_BYPASS_RANGE = "chat.local.bypassrange";

  public static final String STAFF_USE = "chat.staff.use";
  public static final String STAFF_RECEIVE = "chat.staff.receive";

  // Reserved for PR 3 (anti-spam / cooldown):
  //   chat.global.bypasscooldown
  // Reserved for PR 4 (format/colour):
  //   chat.color, chat.format
}
