package com.hanielcota.essentials.modules.clearchat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.clearchat.config.ClearChatConfig;
import com.hanielcota.essentials.modules.clearchat.service.ClearChatService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;

@Command("clearchat")
@Permission("essentials.clearchat")
@Cooldown(duration = "3s")
@Description("Limpa o chat de todos os jogadores online.")
@Syntax("/clearchat")
public record ClearChatCommand(ConfigHandle<ClearChatConfig> config, ClearChatService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Objects.requireNonNull(actor, "actor");

    var snap = config.value();
    String announcement = snap.formatAnnouncement(actor.name());
    service.clearChat(snap.effectiveLines(), announcement);

    // Players see the broadcast above; the console does not, so echo it there.
    if (!actor.isPlayer()) {
      actor.sendMessage(announcement);
    }
  }
}
