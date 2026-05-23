package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.UUID;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;

public record PaperAudienceProvider(EssentialsPlugin plugin) implements AudienceProvider {

  public PaperAudienceProvider(@NonNull EssentialsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public Audience console() {
    var server = this.plugin.getServer();
    return server.getConsoleSender();
  }

  @Override
  public Audience broadcast() {
    return this.plugin.getServer();
  }

  @Override
  public Audience player(@NonNull UUID id) {
    var server = this.plugin.getServer();
    var player = server.getPlayer(id);

    if (player == null) {
      return Audience.empty();
    }

    return player;
  }
}
