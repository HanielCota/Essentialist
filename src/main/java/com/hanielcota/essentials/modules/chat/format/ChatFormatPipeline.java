package com.hanielcota.essentials.modules.chat.format;

import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Orchestrates the three formatting stages — parse (template compile), placeholders (PAPI +
 * affixes), render (MiniMessage deserialize) — into a single call. Drop-in replacement for the
 * former {@code ChatFormatter}; same public surface, but each stage is its own collaborator and
 * unit-testable in isolation.
 *
 * <p>Per call:
 *
 * <ol>
 *   <li>{@link ChatTemplateCompiler#compile(String)} → cached, normalised template.
 *   <li>{@link PlaceholderResolver#apply(Player, String)} → PAPI pass; the output may itself
 *       contain legacy {@code &} codes (e.g. {@code &c[Admin]} from {@code %vault_prefix%}), so we
 *       re-normalise via {@link LegacyTagDictionary#convertLegacy(String, boolean, boolean)} —
 *       short-circuited when no {@code &} is present.
 *   <li>{@link ChatTagResolverFactory#build(Player, Component)} → resolver for player/world/affix
 *       tags.
 *   <li>{@link ChatLineRenderer#render(String,
 *       net.kyori.adventure.text.minimessage.tag.resolver.TagResolver)} → final {@link Component}.
 * </ol>
 */
@RequiredArgsConstructor
public final class ChatFormatPipeline {

  private final ChatTemplateCompiler compiler;
  private final PlaceholderResolver placeholders;
  private final ChatTagResolverFactory resolverFactory;
  private final ChatLineRenderer renderer;

  public Component format(
      @NonNull Player sender, @NonNull Component message, @NonNull String templateSource) {
    var compiled = this.compiler.compile(templateSource);

    var afterPlaceholders = this.placeholders.apply(sender, compiled);
    var renormalised =
        afterPlaceholders.indexOf('&') < 0
            ? afterPlaceholders
            : LegacyTagDictionary.convertLegacy(afterPlaceholders, true, true);

    var resolver = this.resolverFactory.build(sender, message);

    return this.renderer.render(renormalised, resolver);
  }
}
