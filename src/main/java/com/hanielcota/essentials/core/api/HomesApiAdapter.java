package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.HomesApi;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HomesApiAdapter implements HomesApi {

  private final HomeService service;

  @Override
  public List<Home> homesOf(@NonNull UUID owner) {
    return this.service.list(owner);
  }

  @Override
  public Optional<Home> findHome(@NonNull UUID owner, @NonNull String name) {
    return this.service.find(owner, name);
  }

  @Override
  public int homeCount(@NonNull UUID owner) {
    return this.service.count(owner);
  }
}
