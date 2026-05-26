package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface TpaBlockStore {

  List<TpaBlockService.Entry> listAll();

  void save(@NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName);

  void delete(@NonNull UUID blockerId, @NonNull UUID blockedId);
}
