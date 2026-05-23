package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ConfirmDialog;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
    var titleText = config.value().messages().deleteConfirmTitle();
    return ComponentUtils.mini(titleText);
  }

  @Override
  protected @NonNull ItemTemplate promptItem() {
    var promptName = config.value().messages().deleteConfirmPrompt();

    return ItemTemplate.builder(Material.PAPER).name(promptName).italic(false).build();
  }

  @Override
  protected @NonNull ItemTemplate yesButton() {
    var yesName = config.value().messages().deleteConfirmYes();

    return ItemTemplate.builder(Material.LIME_WOOL).name(yesName).italic(false).build();
  }

  @Override
  protected @NonNull ItemTemplate noButton() {
    var noName = config.value().messages().deleteConfirmNo();

    return ItemTemplate.builder(Material.RED_WOOL).name(noName).italic(false).build();
  }

  @Override
  protected void onConfirm(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var homeName = target.consume(uuid);

    if (homeName == null) {
      return;
    }

    var messages = config.value().messages();

    if (!service.delete(uuid, homeName)) {
      var unknownMsg = messages.unknownHome().replace("{name}", homeName);
      player.sendMessage(ComponentUtils.mini(unknownMsg));
      menus.open(player, HomesMenu.ID);
      return;
    }

    var deletedMsg = messages.homeDeleted().replace("{name}", homeName);
    player.sendMessage(ComponentUtils.mini(deletedMsg));
    menus.open(player, HomesMenu.ID);
  }

  @Override
  protected void onCancel(@NonNull Player player) {
    var uuid = player.getUniqueId();

    target.clear(uuid);
    menus.open(player, HomesMenu.ID);
  }
}
