package com.hanielcota.essentials.modules.chat.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Converts legacy ampersand colour codes ({@code &c}, {@code &l}, ...) into the MiniMessage tags
 * the chat formatter understands.
 *
 * <p>Runs once per config reload, never on the chat hot path. The lookup table is a {@code char[]}
 * indexed by the lowercase code character so the conversion is O(n) over the input string with zero
 * allocations per character that is not an ampersand.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatNormalizer {

  // 'r' is the highest legacy code we care about; size the array to its index + 1 so every valid
  // code is a direct lookup with no extra branching.
  private static final int TABLE_SIZE = 'r' + 1;
  private static final String[] LEGACY_TO_MINI = buildTable();

  public static String normalize(@NonNull String input) {
    if (input.indexOf('&') < 0) {
      return input;
    }

    var length = input.length();
    var builder = new StringBuilder(length + 16);

    for (var i = 0; i < length; i++) {
      var c = input.charAt(i);
      if (c != '&' || i + 1 >= length) {
        builder.append(c);
        continue;
      }

      var next = input.charAt(i + 1);
      var lower = Character.toLowerCase(next);
      var tag = lower < TABLE_SIZE ? LEGACY_TO_MINI[lower] : null;
      if (tag == null) {
        builder.append(c);
        continue;
      }

      builder.append(tag);
      i++;
    }

    return builder.toString();
  }

  private static String[] buildTable() {
    var table = new String[TABLE_SIZE];
    table['0'] = "<black>";
    table['1'] = "<dark_blue>";
    table['2'] = "<dark_green>";
    table['3'] = "<dark_aqua>";
    table['4'] = "<dark_red>";
    table['5'] = "<dark_purple>";
    table['6'] = "<gold>";
    table['7'] = "<gray>";
    table['8'] = "<dark_gray>";
    table['9'] = "<blue>";
    table['a'] = "<green>";
    table['b'] = "<aqua>";
    table['c'] = "<red>";
    table['d'] = "<light_purple>";
    table['e'] = "<yellow>";
    table['f'] = "<white>";
    table['k'] = "<obfuscated>";
    table['l'] = "<bold>";
    table['m'] = "<strikethrough>";
    table['n'] = "<underlined>";
    table['o'] = "<italic>";
    table['r'] = "<reset>";

    return table;
  }
}
