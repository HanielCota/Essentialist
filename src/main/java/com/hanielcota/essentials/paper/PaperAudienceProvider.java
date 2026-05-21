package com.hanielcota.essentials.paper;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public record PaperAudienceProvider(EssentialsPlugin plugin) implements AudienceProvider {

  public PaperAudienceProvider {
    Objects.requireNonNull(plugin, "plugin");
  }

  @Override
  public Audience console() {
    return plugin.getServer().getConsoleSender();
  }

  @Override
  public Audience broadcast() {
    return plugin.getServer();
  }

  @Override
  public Audience player(UUID id) {
    Objects.requireNonNull(id, "id");
    Player player = plugin.getServer().getPlayer(id);
    return player == null ? Audience.empty() : player;
  }
}
