package com.hanielcota.essentials.modules.chat.format;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Single source of truth for the legacy ampersand → MiniMessage tag mapping. Both {@link
 * ChatTemplateCompiler} (templates) and {@link PlayerMessageStyler} (typed player messages) read
 * from here, so a new code has exactly one place to land.
 *
 * <p>The lookup is a {@code String[]} indexed by the lowercase code character. Slot is {@code null}
 * when the code is unknown. {@link #COLOR_TAGS} and {@link #DECORATION_TAGS} expose the two halves
 * separately because {@code PlayerMessageStyler} gates them on distinct permissions ({@code
 * chat.color} / {@code chat.format}). {@link #ALL_TAGS} pre-merges both halves for callers that do
 * not care about the split.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LegacyTagDictionary {

  // 'r' is the highest legacy code we care about; size the array to its index + 1 so every valid
  // code is a direct lookup with no extra branching.
  static final int TABLE_SIZE = 'r' + 1;

  public static final String[] COLOR_TAGS = buildColorTable();
  public static final String[] DECORATION_TAGS = buildDecorationTable();
  public static final String[] ALL_TAGS = mergeTables();

  /**
   * Returns the MiniMessage tag for {@code code}, or {@code null} if the character is not a
   * recognised legacy code. {@code allowColors} / {@code allowDecorations} gate which half of the
   * table is consulted; pass both {@code true} to look up any code.
   */
  public static String tagFor(char code, boolean allowColors, boolean allowDecorations) {
    var lower = Character.toLowerCase(code);
    if (lower >= TABLE_SIZE) {
      return null;
    }

    if (allowColors) {
      var colorTag = COLOR_TAGS[lower];
      if (colorTag != null) {
        return colorTag;
      }
    }
    if (allowDecorations) {
      var decorationTag = DECORATION_TAGS[lower];
      if (decorationTag != null) {
        return decorationTag;
      }
    }
    return null;
  }

  /**
   * Walks {@code input} once and rewrites {@code &x} sequences as the corresponding MiniMessage
   * tag, gated by {@code allowColors} / {@code allowDecorations}. Short-circuits when {@code input}
   * has no {@code &}.
   */
  public static String convertLegacy(
      @NonNull String input, boolean allowColors, boolean allowDecorations) {
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

      var tag = tagFor(input.charAt(i + 1), allowColors, allowDecorations);
      if (tag == null) {
        builder.append(c);
        continue;
      }

      builder.append(tag);
      i++;
    }

    return builder.toString();
  }

  private static String[] buildColorTable() {
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

    return table;
  }

  private static String[] buildDecorationTable() {
    var table = new String[TABLE_SIZE];
    table['k'] = "<obfuscated>";
    table['l'] = "<bold>";
    table['m'] = "<strikethrough>";
    table['n'] = "<underlined>";
    table['o'] = "<italic>";
    // <reset> sits in the decoration permission because formatting plugins traditionally treat &r
    // as part of the decoration-control family rather than the colour family.
    table['r'] = "<reset>";

    return table;
  }

  private static String[] mergeTables() {
    var merged = new String[TABLE_SIZE];
    for (var i = 0; i < TABLE_SIZE; i++) {
      merged[i] = COLOR_TAGS[i] != null ? COLOR_TAGS[i] : DECORATION_TAGS[i];
    }
    return merged;
  }
}
