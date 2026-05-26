package com.hanielcota.essentials.modules.chat.permission;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Permission keys recognised by the {@code chat} module.
 *
 * <p>Kept as a single constant holder rather than scattered string literals so a permission rename
 * touches one file instead of every command/listener. Permissions reserved for later PRs (channels,
 * colour, format) are listed here as comments so the doc stays a single source of truth as the
 * module grows.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatPermissions {

  public static final String RELOAD = "chat.reload";
  public static final String ADMIN = "chat.admin";

  // Reserved for PR 2 (channels):
  //   chat.global.use, chat.global.bypasscooldown
  //   chat.local.bypassrange
  //   chat.staff.use, chat.staff.receive
  // Reserved for PR 4 (format/colour):
  //   chat.color, chat.format
}
