package com.hanielcota.essentials.modules.title.service;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Parses a title message into its title and subtitle lines.
 *
 * <p>Format: a leading {@code "} starts the title; the next {@code "..."} pair, if present,
 * provides the subtitle. Anything outside quotes is ignored. A message with no leading quote is
 * taken as the title with an empty subtitle.
 */
record TitleLines(String title, String subtitle) {

  static TitleLines parse(@NonNull String message) {
    var trimmed = message.strip();
    if (!trimmed.startsWith("\"")) {
      return new TitleLines(trimmed, "");
    }

    var quoted = extractQuoted(trimmed);
    var title = quoted.isEmpty() ? "" : quoted.get(0);
    var subtitle = quoted.size() > 1 ? quoted.get(1) : "";

    return new TitleLines(title, subtitle);
  }

  private static List<String> extractQuoted(@NonNull String input) {
    var segments = new ArrayList<String>(2);
    var cursor = 0;

    while (segments.size() < 2) {
      var open = input.indexOf('"', cursor);
      if (open < 0) {
        break;
      }

      var close = input.indexOf('"', open + 1);
      if (close < 0) {
        segments.add(input.substring(open + 1));
        break;
      }

      segments.add(input.substring(open + 1, close));
      cursor = close + 1;
    }

    return segments;
  }
}
