package com.hanielcota.essentials.command;

import com.hanielcota.essentials.command.cooldown.CommandCooldownService;
import com.hanielcota.essentials.command.cooldown.CooldownInterceptor;
import com.hanielcota.essentials.command.cooldown.CooldownsConfig;
import com.hanielcota.essentials.command.interceptor.AuditInterceptor;
import com.hanielcota.essentials.config.ConfigHandle;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import io.papermc.paper.registry.RegistryAccess;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires the command framework: messages, interceptor, enum aliases, suggestion providers and
 * exception handlers. Dependencies are constructor-injected so the bootstrap is testable and
 * extensible without modifying source.
 */
@RequiredArgsConstructor
public final class CommandBootstrap {

  private static final String ILLEGAL_ARG_TEMPLATE = "<red>Erro: ";
  private static final String UNEXPECTED_TEMPLATE = "<red>Ocorreu um erro inesperado.";

  private final @NonNull JavaPlugin plugin;
  private final @NonNull RegistryAccess registryAccess;
  private final @NonNull ConfigHandle<CooldownsConfig> cooldownConfig;

  public PaperCommandFramework createFramework() {
    var logger = this.plugin.getLogger();

    var messageProvider =
        io.github.hanielcota.commandframework.core.message.CommandMessages.portugueseBrazil();
    var auditInterceptor = new AuditInterceptor(logger);
    var cooldownService = new CommandCooldownService();
    var cooldownInterceptor = new CooldownInterceptor(this.cooldownConfig, cooldownService);

    var rawBuilder = PaperCommandFramework.builder(this.plugin);
    var withMessages = rawBuilder.messageProvider(messageProvider);
    var withAudit = withMessages.interceptor(auditInterceptor);
    var builder = withAudit.interceptor(cooldownInterceptor);

    registerGameModeAliases(builder);

    var enchantmentProvider = EnchantmentSuggestions.provider(this.registryAccess);
    builder.suggestionProvider("enchantments", enchantmentProvider);

    CommandExceptionHandler.register(builder, logger, ILLEGAL_ARG_TEMPLATE, UNEXPECTED_TEMPLATE);

    return builder.build();
  }

  private void registerGameModeAliases(PaperCommandFramework.Builder builder) {
    builder.enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0");
    builder.enumAlias(GameMode.CREATIVE, "criativo", "c", "1");
    builder.enumAlias(GameMode.ADVENTURE, "aventura", "a", "2");
    builder.enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3");
  }
}
