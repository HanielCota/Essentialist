package com.hanielcota.essentials.modules.spawn.repository;

import com.hanielcota.essentials.modules.spawn.domain.SpawnLocation;
import java.util.Optional;
import lombok.NonNull;

public interface SpawnStore {

  Optional<SpawnLocation> load();

  void save(@NonNull SpawnLocation location);
}
