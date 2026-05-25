package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.NicksApi;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.service.NickService;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NicksApiAdapter implements NicksApi {

  private final NickService service;

  @Override
  public Optional<NickEntry> nickOf(@NonNull UUID id) {
    return this.service.nickFor(id);
  }

  @Override
  public Optional<UUID> idByNick(@NonNull String nickname) {
    return this.service.idByNick(nickname);
  }
}
