package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ListMenu;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class BackMenu extends ListMenu<HistoryEntry> {

  public static final String ID = "essentials.back";

  private final ConfigHandle<BackConfig> config;
  private final TeleportHistory history;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;

  public BackMenu(
      ConfigHandle<BackConfig> config,
      TeleportHistory history,
      BackEntryRenderer renderer,
      BackClickHandler clickHandler) {
    this.config = Objects.requireNonNull(config, "config");
    this.history = Objects.requireNonNull(history, "history");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  protected int rows() {
    return config.value().menuRows();
  }

  @Override
  protected @NonNull Component title() {
    return ComponentUtils.mini(config.value().menuTitle());
  }

  @Override
  protected @NonNull List<HistoryEntry> items(@NonNull Player player) {
    return history.list(player.getUniqueId());
  }

  @Override
  protected @NonNull ItemTemplate render(@NonNull HistoryEntry entry, int humanIndex) {
    return renderer.render(entry, humanIndex);
  }

  @Override
  protected void onClick(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    clickHandler.handle(click, entry);
  }
}
