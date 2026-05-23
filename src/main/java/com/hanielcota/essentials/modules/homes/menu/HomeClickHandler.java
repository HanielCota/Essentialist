package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.Home;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

/**
 * Routes a /homes menu click to one of four flows by {@link org.bukkit.event.inventory.ClickType}:
 * left = teleport, right = delete dialog, shift+click = rename prompt, drop = material picker.
 */
@RequiredArgsConstructor
public final class HomeClickHandler implements ItemClickHandler<Home> {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final DelayedTeleport delayed;
  private final PaperCommandFramework framework;
  private final HomesActionTarget target;
  private final HomeRenamePrompter rename;

  @Override
  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var clickType = click.clickType();

    if (clickType.isShiftClick()) {
      click.close();
      rename.prompt(click.player(), home.name());
      return;
    }
    if (clickType == org.bukkit.event.inventory.ClickType.DROP
        || clickType == org.bukkit.event.inventory.ClickType.CONTROL_DROP) {
      target.set(click.player().getUniqueId(), home.name());
      click.open(MaterialPickerMenu.ID);
      return;
    }
    if (clickType.isRightClick()) {
      target.set(click.player().getUniqueId(), home.name());
      click.open(DeleteHomeDialog.ID);
      return;
    }

    teleport(click, home);
  }

  private void teleport(ClickContext click, Home home) {
    var snap = config.value();
    var messages = snap.messages();
    var resolved = home.resolve();

    if (resolved.isEmpty()) {
      click.close();
      click.reply(messages.worldGone());
      return;
    }

    click.close();
    var actor = framework.actorOf(click.player());
    delayed.schedule(
        click.player(), resolved.get(), snap.teleportDelay(), prompt(actor, messages, home));
  }

  private static DelayedTeleportPrompt prompt(
      io.github.hanielcota.commandframework.core.CommandActor actor,
      HomesMessages messages,
      Home home) {
    return new DelayedTeleportPrompt(
        actor,
        messages.teleporting().replace("{name}", home.name()),
        messages.teleported().replace("{name}", home.name()),
        messages.cancelled(),
        messages.failed());
  }
}
