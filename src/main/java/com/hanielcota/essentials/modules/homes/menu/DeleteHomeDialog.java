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
    var titleText = this.config.value().messages().deleteConfirmTitle();
    return ComponentUtils.mini(titleText);
  }

  @Override
  protected @NonNull ItemTemplate promptItem() {
    var promptName = this.config.value().messages().deleteConfirmPrompt();

    return ItemTemplate.builder(Material.PAPER).name(promptName).italic(false).build();
  }

  @Override
  protected @NonNull ItemTemplate yesButton() {
    var yesName = this.config.value().messages().deleteConfirmYes();

    return ItemTemplate.builder(Material.LIME_WOOL).name(yesName).italic(false).build();
  }

  @Override
  protected @NonNull ItemTemplate noButton() {
    var noName = this.config.value().messages().deleteConfirmNo();

    return ItemTemplate.builder(Material.RED_WOOL).name(noName).italic(false).build();
  }

  @Override
  protected void onConfirm(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var homeName = this.target.consume(uuid);

    if (homeName == null) {
      return;
    }

    var messages = this.config.value().messages();

    if (!this.service.delete(uuid, homeName)) {
      var unknownMsg = messages.unknownHome().replace("{name}", homeName);
      player.sendMessage(ComponentUtils.mini(unknownMsg));
      this.menus.open(player, HomesMenu.ID);
      return;
    }

    var deletedMsg = messages.homeDeleted().replace("{name}", homeName);
    player.sendMessage(ComponentUtils.mini(deletedMsg));
    this.menus.open(player, HomesMenu.ID);
  }

  @Override
  protected void onCancel(@NonNull Player player) {
    var uuid = player.getUniqueId();

    this.target.clear(uuid);
    this.menus.open(player, HomesMenu.ID);
  }
}
