package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.VanishApi;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class VanishApiAdapter implements VanishApi {

  private final VanishService service;

  @Override
  public boolean isVanished(@NonNull UUID id) {
    return this.service.isVanished(id);
  }

  @Override
  public Set<UUID> vanished() {
    return this.service.vanished();
  }
}
