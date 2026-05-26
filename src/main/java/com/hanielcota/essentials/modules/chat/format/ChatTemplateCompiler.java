package com.hanielcota.essentials.modules.chat.format;

import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Parses raw chat templates into a normalised MiniMessage-ready form and caches the result.
 *
 * <p>Pure {@code String → String} transformation — no Bukkit, no placeholders, no MiniMessage
 * deserialisation. Each template source is compiled once and reused for every message that uses the
 * same template, which makes the hot path a single {@link ConcurrentHashMap#computeIfAbsent}
 * lookup.
 *
 * <p>The cache is unbounded but grows by at most one entry per distinct template string the config
 * exposes (today: three — global/local/staff). Config reloads do not invalidate the cache because
 * the cache key is the template string itself; a changed template gets a new entry, and the
 * obsolete one becomes unreferenced after the next message uses the new template.
 */
public final class ChatTemplateCompiler {

  private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

  public String compile(@NonNull String templateSource) {
    return this.cache.computeIfAbsent(templateSource, ChatTemplateCompiler::normalize);
  }

  private static String normalize(@NonNull String source) {
    return LegacyTagDictionary.convertLegacy(source, true, true);
  }
}
