package com.hanielcota.essentials.modules.online.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ListMenu;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/** Menu that lists every online player as a head, one per slot. */
public final class OnlineMenu extends ListMenu<Player> {

  public static final String id = "essentials.online";

  private final ConfigHandle<OnlineConfig> config;

  public OnlineMenu(ConfigHandle<OnlineConfig> config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  @Override
  public @NonNull String id() {
    return id;
  }

  @Override
  protected int rows() {
    return config.value().sanitizedRows();
  }

  @Override
  protected @NonNull Component title() {
    var onlineCount = Bukkit.getOnlinePlayers().size();
    var formattedTitle = config.value().formatTitle(onlineCount);

    return ComponentUtils.mini(formattedTitle);
  }

  @Override
  protected @NonNull List<Player> items(@NonNull Player viewer) {
    return List.copyOf(Bukkit.getOnlinePlayers());
  }

  @Override
  protected @NonNull ItemTemplate render(@NonNull Player player, int humanIndex) {
    var snap = config.value();

    var name = snap.formatItemName(player.getName());
    var lore = snap.formatItemLore(player.getName(), player.getPing(), player.getWorld().getName());

    return ItemTemplate.builder(Material.PLAYER_HEAD)
        .name(name)
        .head(player.getUniqueId())
        .lore(lore.toArray(String[]::new))
        .italic(false)
        .build();
  }

  @Override
  protected void onClick(@NonNull ClickContext click, @NonNull Player player) {
    // Informational menu — clicking a head does nothing.
  }
}
