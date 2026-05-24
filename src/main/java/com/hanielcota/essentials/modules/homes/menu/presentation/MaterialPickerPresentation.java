package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class MaterialPickerPresentation {

  private final ConfigHandle<MaterialNamesConfig> materialNames;

  public String reply(
      @NonNull HomesMessages messages,
      @NonNull String homeName,
      @NonNull Material material,
      boolean applied) {

    if (!applied) {
      return messages.unknownHome().replace("{name}", homeName);
    }

    var pretty = this.materialNames.value().displayName(material);

    return messages.materialUpdated().replace("{name}", homeName).replace("{material}", pretty);
  }
}
