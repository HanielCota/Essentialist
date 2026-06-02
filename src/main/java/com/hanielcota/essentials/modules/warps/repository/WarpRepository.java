package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import lombok.NonNull;

public interface WarpRepository {

  List<Warp> list();

  void save(@NonNull Warp warp);

  boolean delete(@NonNull String name);
}
