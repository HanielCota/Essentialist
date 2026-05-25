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
      var unknownHomeMsg = messages.unknownHome();
      return unknownHomeMsg.replace("{name}", homeName);
    }

    var snap = this.materialNames.value();
    var pretty = snap.displayName(material);

    var materialUpdatedMsg = messages.materialUpdated();
    var withName = materialUpdatedMsg.replace("{name}", homeName);

    return withName.replace("{material}", pretty);
  }
}
