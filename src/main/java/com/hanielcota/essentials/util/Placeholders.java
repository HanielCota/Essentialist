package com.hanielcota.essentials.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces {@code {key}} tokens in a template with their values in a single pass.
 *
 * <p>Use over chained {@link String#replace(CharSequence, CharSequence)} calls when a message has
 * more than one placeholder — chained replaces allocate an intermediate string per call and scan
 * the template once per call, while {@code format} walks it once.
 */
public final class Placeholders {

  private static final Pattern TOKEN = Pattern.compile("\\{([A-Za-z0-9_]+)\\}");

  private Placeholders() {}

  public static String format(String template, String key, Object value) {
    return format(template, Map.of(key, value));
  }

  public static String format(String template, String k1, Object v1, String k2, Object v2) {
    return format(template, Map.of(k1, v1, k2, v2));
  }

  public static String format(
      String template, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return format(template, Map.of(k1, v1, k2, v2, k3, v3));
  }

  /**
   * Replaces every {@code {key}} that has an entry in {@code values}; unknown tokens stay verbatim.
   */
  public static String format(String template, Map<String, ?> values) {
    Matcher matcher = TOKEN.matcher(template);
    StringBuilder out = new StringBuilder(template.length());
    while (matcher.find()) {
      Object replacement = values.get(matcher.group(1));
      matcher.appendReplacement(
          out,
          replacement == null
              ? Matcher.quoteReplacement(matcher.group())
              : Matcher.quoteReplacement(replacement.toString()));
    }
    matcher.appendTail(out);
    return out.toString();
  }
}
