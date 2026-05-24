package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import lombok.NonNull;
import org.bukkit.Material;

public final class MaterialPickerPresentation {

  public String reply(
      @NonNull HomesMessages messages,
      @NonNull String homeName,
      @NonNull Material material,
      boolean applied) {

    if (!applied) {
      return messages.unknownHome().replace("{name}", homeName);
    }

    var prettyMaterial = MaterialNames.pretty(material);

    return messages
        .materialUpdated()
        .replace("{name}", homeName)
        .replace("{material}", prettyMaterial);
  }
}
