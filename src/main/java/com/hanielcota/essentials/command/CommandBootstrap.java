package com.hanielcota.essentials.command;

import com.hanielcota.essentials.command.interceptor.AuditInterceptor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the command framework: messages, interceptor, enum aliases, suggestion providers and
 * exception handlers. Domain-specific logic lives in {@link EnchantmentSuggestions} and {@link
 * CommandExceptionHandler}.
 */
@RequiredArgsConstructor
public final class CommandBootstrap {

  private final @NonNull JavaPlugin plugin;

  public PaperCommandFramework createFramework() {
    var logger = this.plugin.getLogger();

    var messageProvider =
        io.github.hanielcota.commandframework.core.message.CommandMessages.portugueseBrazil();
    var auditInterceptor = new AuditInterceptor(logger);

    var rawBuilder = PaperCommandFramework.builder(this.plugin);
    var withMessages = rawBuilder.messageProvider(messageProvider);
    var builder = withMessages.interceptor(auditInterceptor);

    builder.enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0");
    builder.enumAlias(GameMode.CREATIVE, "criativo", "c", "1");
    builder.enumAlias(GameMode.ADVENTURE, "aventura", "a", "2");
    builder.enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3");

    var enchantmentProvider = EnchantmentSuggestions.provider();
    builder.suggestionProvider("enchantments", enchantmentProvider);

    CommandExceptionHandler.register(builder, logger);

    return builder.build();
  }
}
