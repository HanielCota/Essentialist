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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class CommandBootstrap {

  private final @NonNull JavaPlugin plugin;
  private final @NonNull List<Consumer<PaperCommandFramework.Builder>> customizers;

  // Tab-completes enchantment names (without the `minecraft:` namespace) by prefix.
  private static SuggestionProvider<Enchantment> enchantmentSuggestions() {
    return context -> {
      var currentInput = context.currentInput();
      var input = currentInput.toLowerCase(Locale.ROOT);

      var names = new ArrayList<String>();
      var registryAccess = RegistryAccess.registryAccess();
      var enchantmentRegistry = registryAccess.getRegistry(RegistryKey.ENCHANTMENT);

      for (var enchantment : enchantmentRegistry) {
        var namespacedKey = enchantment.getKey();
        var name = namespacedKey.getKey();

        if (name.startsWith(input)) {
          names.add(name);
        }
      }

      return names;
    };
  }

  public PaperCommandFramework createFramework() {
    var logger = this.plugin.getLogger();

    var builder =
        PaperCommandFramework.builder(this.plugin)
            .messageProvider(CommandMessages.portugueseBrazil())
            .interceptor(new AuditInterceptor(logger));

    builder.enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0");
    builder.enumAlias(GameMode.CREATIVE, "criativo", "c", "1");
    builder.enumAlias(GameMode.ADVENTURE, "aventura", "a", "2");
    builder.enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3");

    var enchantmentProvider = enchantmentSuggestions();
    builder.suggestionProvider("enchantments", enchantmentProvider);

    builder.onException(
        IllegalArgumentException.class,
        (ctx, ex) -> {
          var actor = ctx.actor();
          var errorMessage = ex.getMessage();

          actor.sendError("<red>Erro: " + errorMessage);
          return CommandResult.failure(CommandStatus.INVALID_USAGE, errorMessage);
        });

    builder.onException(
        RuntimeException.class,
        (ctx, ex) -> {
          var actor = ctx.actor();
          actor.sendError("<red>Ocorreu um erro inesperado.");

          var pluginLogger = this.plugin.getLogger();
          pluginLogger.log(Level.WARNING, ex, () -> "Unhandled command exception");

          return CommandResult.failure(CommandStatus.ERROR, "unexpected");
        });

    for (var customizer : this.customizers) {
      customizer.accept(builder);
    }

    return builder.build();
  }
}
