package com.hanielcota.essentials.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

  public static String format(
      @NonNull String template,
      @NonNull String k1,
      @NonNull Object v1,
      @NonNull String k2,
      @NonNull Object v2,
      @NonNull String k3,
      @NonNull Object v3,
      @NonNull String k4,
      @NonNull Object v4) {
    var valuesMap = Map.of(k1, v1, k2, v2, k3, v3, k4, v4);
    return format(template, valuesMap);
  }

  /**
   * Single-token list-wise replacement: returns a fresh list where every line has {@code token}
   * swapped for {@code replacement}. Use this when the same lore template needs to be expanded for
   * multiple viewers — call sites avoid hand-rolling a loop over {@link String#replace}.
   */
  public static List<String> replaceInAll(
      @NonNull List<String> templates, @NonNull String token, @NonNull String replacement) {
    var result = new ArrayList<String>(templates.size());
    for (var line : templates) {
      result.add(line.replace(token, replacement));
    }
    return result;
  }

  public static String format(@NonNull String template, @NonNull Map<String, ?> values) {
    var matcher = TOKEN.matcher(template);
    var templateLength = template.length();
    var result = new StringBuilder(templateLength);

    while (matcher.find()) {
      var tokenKey = matcher.group(1);
      var replacementValue = values.get(tokenKey);

      var rawReplacementStr = resolveReplacement(matcher, replacementValue);
      var safeReplacement = Matcher.quoteReplacement(rawReplacementStr);

      matcher.appendReplacement(result, safeReplacement);
    }

    matcher.appendTail(result);
    return result.toString();
  }

  private static String resolveReplacement(@NonNull Matcher matcher, Object replacementValue) {
    if (replacementValue == null) {
      return matcher.group();
    }

    return replacementValue.toString();
  }
}
