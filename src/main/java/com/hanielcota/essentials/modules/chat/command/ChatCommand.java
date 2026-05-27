package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;

/**
 * Admin entry point for the chat module — currently only {@code /chat reload}, which delegates to
 * {@link ConfigService#reloadAll()} so the report covers every module's config in one pass and
 * matches the behaviour of {@code /essentials reload}.
 */
@Command("chat")
@Permission(ChatPermissions.ADMIN)
@Description("Administrative commands for the chat module.")
@Syntax("/chat reload")
public record ChatCommand(ConfigService configs, ChatNotifier notifier) {

  @DefaultSubcommand
  public CommandResult showUsage(@NonNull CommandActor actor) {
    this.notifier.sendUsage(actor);
    return CommandResult.success();
  }

  @Subcommand("reload")
  @Permission(ChatPermissions.RELOAD)
  @Description("Reload every module config — chat templates are re-normalised lazily on first use.")
  @Syntax("/chat reload")
  public CommandResult reload(@NonNull CommandActor actor) {
    var report = this.configs.reloadAll();

    if (report.failures().isEmpty()) {
      var total = report.total();
      this.notifier.sendReloadSuccess(actor, total);
      return CommandResult.success();
    }

    var failedNames = report.failedNames();
    var failed = String.join(", ", failedNames);
    var succeeded = report.succeeded();
    var total = report.total();

    this.notifier.sendReloadFailure(actor, succeeded, total, failed);
    return CommandResult.success();
  }
}
