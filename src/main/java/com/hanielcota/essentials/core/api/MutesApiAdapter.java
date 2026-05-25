package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.MutesApi;
import com.hanielcota.essentials.modules.mute.model.Mute;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MutesApiAdapter implements MutesApi {

  private final MuteService service;

  @Override
  public boolean isMuted(@NonNull UUID id) {
    return this.service.activeMute(id).isPresent();
  }

  @Override
  public Optional<Mute> activeMute(@NonNull UUID id) {
    return this.service.activeMute(id);
  }
}
