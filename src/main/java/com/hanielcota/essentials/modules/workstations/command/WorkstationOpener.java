package com.hanielcota.essentials.modules.workstations.command;

import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WorkstationOpener {

  static void open(@NonNull CommandActor actor, @NonNull MenuType.Typed<?, ?> menuType) {
    var player = actor.unwrap(Player.class);
    var menu = menuType.create(player);

    player.openInventory(menu);
  }
}
