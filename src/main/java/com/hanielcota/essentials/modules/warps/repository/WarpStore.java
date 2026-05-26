package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface WarpStore {

  Optional<Warp> find(@NonNull String name);

  List<Warp> list();

  void save(@NonNull Warp warp);

  boolean delete(@NonNull String name);
}
