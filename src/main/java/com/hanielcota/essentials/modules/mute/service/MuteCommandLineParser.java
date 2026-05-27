package com.hanielcota.essentials.modules.mute.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Strips slash, args and namespace prefix from a raw command line so the result can be matched
 * against {@code MuteConfig#isBlockedCommand}. {@code "/minecraft:me hello"} → {@code "me"}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MuteCommandLineParser {

  public static String canonicalName(@NonNull String rawMessage) {
    var withoutSlash = rawMessage.startsWith("/") ? rawMessage.substring(1) : rawMessage;
    var space = withoutSlash.indexOf(' ');
    var firstToken = space < 0 ? withoutSlash : withoutSlash.substring(0, space);

    var colon = firstToken.indexOf(':');
    return colon < 0 ? firstToken : firstToken.substring(colon + 1);
  }
}
