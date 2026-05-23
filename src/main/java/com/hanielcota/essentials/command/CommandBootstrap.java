package com.hanielcota.essentials.command;

import com.hanielcota.essentials.command.interceptor.AuditInterceptor;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.core.SuggestionProvider;
import io.github.hanielcota.commandframework.core.message.CommandMessages;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandBootstrap {

  private final JavaPlugin plugin;
  private final List<Consumer<PaperCommandFramework.Builder>> customizers;

  public CommandBootstrap(
      JavaPlugin plugin, List<Consumer<PaperCommandFramework.Builder>> customizers) {
    this.plugin = plugin;
    this.customizers = List.copyOf(customizers);
  }

  // Tab-completes enchantment names (without the `minecraft:` namespace) by prefix.
  private static SuggestionProvider<Enchantment> enchantmentSuggestions() {
    return context -> {
      var input = context.currentInput().toLowerCase(Locale.ROOT);
      var names = new ArrayList<String>();

      for (var enchantment :
          RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)) {
        var name = enchantment.getKey().getKey();
        if (name.startsWith(input)) {
          names.add(name);
        }
      }
      return names;
    };
  }

  public PaperCommandFramework createFramework() {
    var builder =
        PaperCommandFramework.builder(plugin)
            .messageProvider(CommandMessages.portugueseBrazil())
            .interceptor(new AuditInterceptor(plugin.getLogger()));

    builder
        .enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0")
        .enumAlias(GameMode.CREATIVE, "criativo", "c", "1")
        .enumAlias(GameMode.ADVENTURE, "aventura", "a", "2")
        .enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3");

    builder.suggestionProvider("enchantments", enchantmentSuggestions());

    builder
        .onException(
            IllegalArgumentException.class,
            (ctx, ex) -> {
              ctx.actor().sendError("<red>Erro: " + ex.getMessage());
              return CommandResult.failure(CommandStatus.INVALID_USAGE, ex.getMessage());
            })
        .onException(
            RuntimeException.class,
            (ctx, ex) -> {
              ctx.actor().sendError("<red>Ocorreu um erro inesperado.");
              plugin.getLogger().log(Level.WARNING, ex, () -> "Unhandled command exception");
              return CommandResult.failure(CommandStatus.ERROR, "unexpected");
            });

    for (var customizer : customizers) {
      customizer.accept(builder);
    }

    return builder.build();
  }
}
