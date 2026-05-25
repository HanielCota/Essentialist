package com.hanielcota.essentials.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Replaces {@code {key}} tokens in a template with their values in a single pass.
 *
 * <p>Use over chained {@link String#replace(CharSequence, CharSequence)} calls when a message has
 * more than one placeholder — chained replaces allocate an intermediate string per call and scan
 * the template once per call, while {@code format} walks it once.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Placeholders {

  private static final Pattern TOKEN = Pattern.compile("\\{(\\w+)\\}");

  public static String format(
      @NonNull String template, @NonNull String key, @NonNull Object value) {
    var valuesMap = Map.of(key, value);
    return format(template, valuesMap);
  }

  public static String format(
      @NonNull String template,
      @NonNull String k1,
      @NonNull Object v1,
      @NonNull String k2,
      @NonNull Object v2) {
    var valuesMap = Map.of(k1, v1, k2, v2);
    return format(template, valuesMap);
  }

  public static String format(
      @NonNull String template,
      @NonNull String k1,
      @NonNull Object v1,
      @NonNull String k2,
      @NonNull Object v2,
      @NonNull String k3,
      @NonNull Object v3) {
    var valuesMap = Map.of(k1, v1, k2, v2, k3, v3);
    return format(template, valuesMap);
  }

  /**
   * Replaces every {@code {key}} that has an entry in {@code values}; unknown tokens stay verbatim.
   */
  public static String format(@NonNull String template, @NonNull Map<String, ?> values) {
    var matcher = TOKEN.matcher(template);
    var templateLength = template.length();
    var out = new StringBuilder(templateLength);

    while (matcher.find()) {
      var tokenKey = matcher.group(1);
      var replacementValue = values.get(tokenKey);

      var rawReplacementStr = resolveReplacement(matcher, replacementValue);
      var safeReplacement = Matcher.quoteReplacement(rawReplacementStr);

      matcher.appendReplacement(out, safeReplacement);
    }

    matcher.appendTail(out);
    return out.toString();
  }

  private static String resolveReplacement(@NonNull Matcher matcher, Object replacementValue) {
    if (replacementValue == null) {
      return matcher.group();
    }

    return replacementValue.toString();
  }
}
