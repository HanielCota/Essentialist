package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ConfirmDialog;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Yes/no confirmation before deleting a home. The home to act on is read from {@link
 * HomesActionTarget} on click, set by {@link HomeClickHandler} when the player right-clicks a home.
 */
@RequiredArgsConstructor
public final class DeleteHomeDialog extends ConfirmDialog {

  public static final String ID = "essentials.homes.delete";

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final MenuService menus;
  private final HomesActionTarget target;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  protected @NonNull Component title() {
    return ComponentUtils.mini(config.value().messages().deleteConfirmTitle());
  }

  @Override
  protected @NonNull ItemTemplate promptItem() {
    return ItemTemplate.builder(Material.PAPER)
        .name(config.value().messages().deleteConfirmPrompt())
        .italic(false)
        .build();
  }

  @Override
  protected @NonNull ItemTemplate yesButton() {
    return ItemTemplate.builder(Material.LIME_WOOL)
        .name(config.value().messages().deleteConfirmYes())
        .italic(false)
        .build();
  }

  @Override
  protected @NonNull ItemTemplate noButton() {
    return ItemTemplate.builder(Material.RED_WOOL)
        .name(config.value().messages().deleteConfirmNo())
        .italic(false)
        .build();
  }

  @Override
  protected void onConfirm(@NonNull Player player) {
    var homeName = target.consume(player.getUniqueId()).orElse(null);
    if (homeName == null) return;

    var messages = config.value().messages();
    if (service.delete(player.getUniqueId(), homeName)) {
      player.sendMessage(ComponentUtils.mini(messages.homeDeleted().replace("{name}", homeName)));
    } else {
      player.sendMessage(ComponentUtils.mini(messages.unknownHome().replace("{name}", homeName)));
    }

    menus.open(player, HomesMenu.ID);
  }

  @Override
  protected void onCancel(@NonNull Player player) {
    target.clear(player.getUniqueId());
    menus.open(player, HomesMenu.ID);
  }
}
