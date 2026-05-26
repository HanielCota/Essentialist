package com.hanielcota.essentials.command;

import com.hanielcota.essentials.command.interceptor.AuditInterceptor;
import io.github.hanielcota.commandframework.core.CommandContext;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.core.SuggestionProvider;
import io.github.hanielcota.commandframework.core.message.CommandMessages;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Level;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class CommandBootstrap {

  private final @NonNull JavaPlugin plugin;

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

    var messageProvider = CommandMessages.portugueseBrazil();
    var auditInterceptor = new AuditInterceptor(logger);

    var rawBuilder = PaperCommandFramework.builder(this.plugin);
    var withMessages = rawBuilder.messageProvider(messageProvider);
    var builder = withMessages.interceptor(auditInterceptor);

    builder.enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0");
    builder.enumAlias(GameMode.CREATIVE, "criativo", "c", "1");
    builder.enumAlias(GameMode.ADVENTURE, "aventura", "a", "2");
    builder.enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3");

    var enchantmentProvider = enchantmentSuggestions();
    builder.suggestionProvider("enchantments", enchantmentProvider);

    registerExceptionHandlers(builder);

    return builder.build();
  }

  // The framework's CommandExceptionHandler signature is (CommandContext, RuntimeException),
  // so each handler method must accept the supertype even when the registered Class<?> picks
  // a narrower branch — the framework dispatches by the registered class, not by the method
  // parameter type.
  private void registerExceptionHandlers(@NonNull PaperCommandFramework.Builder builder) {
    builder.onException(IllegalArgumentException.class, this::handleIllegalArgument);
    builder.onException(RuntimeException.class, this::handleUnexpected);
  }

  private CommandResult handleIllegalArgument(CommandContext ctx, RuntimeException ex) {
    var actor = ctx.actor();
    var errorMessage = ex.getMessage();

    var displayMessage = "<red>Erro: " + errorMessage;
    actor.sendError(displayMessage);

    return CommandResult.failure(CommandStatus.INVALID_USAGE, errorMessage);
  }

  private CommandResult handleUnexpected(CommandContext ctx, RuntimeException ex) {
    var actor = ctx.actor();
    actor.sendError("<red>Ocorreu um erro inesperado.");

    var pluginLogger = this.plugin.getLogger();
    Supplier<String> messageSupplier = () -> "Unhandled command exception";
    pluginLogger.log(Level.WARNING, ex, messageSupplier);

    return CommandResult.failure(CommandStatus.ERROR, "unexpected");
  }
}
