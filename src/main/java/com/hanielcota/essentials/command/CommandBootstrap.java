package com.hanielcota.essentials.command;

import com.hanielcota.essentials.command.interceptor.AuditInterceptor;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import io.github.hanielcota.commandframework.core.message.CommandMessages;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandBootstrap {

  private final JavaPlugin plugin;
  private final List<Consumer<PaperCommandFramework.Builder>> customizers;

  public CommandBootstrap(JavaPlugin plugin) {
    this(plugin, List.of());
  }

  public CommandBootstrap(
      JavaPlugin plugin, List<Consumer<PaperCommandFramework.Builder>> customizers) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.customizers = List.copyOf(Objects.requireNonNull(customizers, "customizers"));
  }

  public PaperCommandFramework createFramework() {
    var builder =
        PaperCommandFramework.builder(plugin)
            .messageProvider(CommandMessages.portugueseBrazil())
            .interceptor(new AuditInterceptor(plugin.getLogger()))
            .enumAlias(GameMode.SURVIVAL, "sobrevivência", "s", "0")
            .enumAlias(GameMode.CREATIVE, "criativo", "c", "1")
            .enumAlias(GameMode.ADVENTURE, "aventura", "a", "2")
            .enumAlias(GameMode.SPECTATOR, "espectador", "sp", "3")
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
                  plugin
                      .getLogger()
                      .log(
                          Level.WARNING,
                          ex,
                          () -> "Unhandled command exception: " + ex.getMessage());
                  return CommandResult.failure(CommandStatus.ERROR, "unexpected");
                });

    for (Consumer<PaperCommandFramework.Builder> customizer : customizers) {
      customizer.accept(builder);
    }
    return builder.build();
  }
}
