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
  public static final String GLOBAL_BYPASS_COOLDOWN = "chat.global.bypasscooldown";

  public static final String LOCAL_BYPASS_RANGE = "chat.local.bypassrange";
  public static final String LOCAL_BYPASS_COOLDOWN = "chat.local.bypasscooldown";

  public static final String STAFF_USE = "chat.staff.use";
  public static final String STAFF_RECEIVE = "chat.staff.receive";
  public static final String STAFF_BYPASS_COOLDOWN = "chat.staff.bypasscooldown";

  public static final String BYPASS_ANTISPAM = "chat.bypassantispam";

  public static final String CHAT_COLOR = "chat.color";
  public static final String CHAT_FORMAT = "chat.format";
}
