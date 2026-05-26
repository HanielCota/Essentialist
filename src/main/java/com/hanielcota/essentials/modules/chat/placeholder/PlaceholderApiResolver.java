package com.hanielcota.essentials.modules.chat.placeholder;

import com.hanielcota.essentials.util.Log;
import java.lang.reflect.Method;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Reflection-based {@link PlaceholderResolver} implementation backed by the PlaceholderAPI plugin.
 * Bound at construction: if {@code PlaceholderAPI} is present we look up {@code
 * PlaceholderAPI.setPlaceholders(Player, String)} once and cache the {@link Method}. Every call
 * site then becomes a static-method invoke; we never reference the PAPI class directly, so the
 * project compiles and ships without any compile- or runtime-dependency on PAPI's jar.
 *
 * <p>When PAPI is missing, {@link #isAvailable()} returns {@code false} and {@link #apply} returns
 * the input unchanged — callers should detect that via {@link #isAvailable()} and skip optional
 * lookups (e.g. prefix/suffix tags) rather than handling a "maybe-it-worked" path.
 *
 * <p>Reflection cost: one {@link Method#invoke} per call. PAPI's {@code setPlaceholders} is itself
 * a regex pass, so the reflective dispatch adds a small constant on top — far below the cost of the
 * actual placeholder substitution. We do not micro-optimise (e.g. {@code MethodHandle}) because
 * PAPI itself dominates the budget.
 */
public final class PlaceholderApiResolver implements PlaceholderResolver {

  private static final Log LOG = Log.of(PlaceholderApiResolver.class);

  private static final String PLUGIN_NAME = "PlaceholderAPI";
  private static final String CLASS_NAME = "me.clip.placeholderapi.PlaceholderAPI";
  private static final String METHOD_NAME = "setPlaceholders";

  private final @Nullable Method setPlaceholders;

  public PlaceholderApiResolver() {
    this.setPlaceholders = resolveMethod();
  }

  @Override
  public boolean isAvailable() {
    return this.setPlaceholders != null;
  }

  @Override
  public String apply(@NonNull Player sender, @NonNull String input) {
    if (this.setPlaceholders == null) {
      return input;
    }
    if (input.indexOf('%') < 0) {
      return input;
    }

    try {
      var result = this.setPlaceholders.invoke(null, sender, input);
      return (String) result;
    } catch (ReflectiveOperationException e) {
      LOG.warn(e, "PlaceholderAPI dispatch failed; falling back to unprocessed template");
      return input;
    }
  }

  private static @Nullable Method resolveMethod() {
    var pluginManager = Bukkit.getServer().getPluginManager();
    var plugin = pluginManager.getPlugin(PLUGIN_NAME);
    if (plugin == null) {
      LOG.info("PlaceholderAPI not detected — chat placeholders run with internal tags only");
      return null;
    }

    try {
      var clazz = Class.forName(CLASS_NAME);
      var method = clazz.getMethod(METHOD_NAME, Player.class, String.class);
      LOG.info("PlaceholderAPI detected — chat placeholders enabled");
      return method;
    } catch (ReflectiveOperationException e) {
      LOG.warn(
          e,
          "PlaceholderAPI plugin present but {} not reachable via reflection; placeholders"
              + " disabled",
          CLASS_NAME);
      return null;
    }
  }
}
