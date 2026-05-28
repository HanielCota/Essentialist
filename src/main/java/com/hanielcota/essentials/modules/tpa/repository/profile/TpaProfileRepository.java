package com.hanielcota.essentials.modules.tpa.repository.profile;

import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface TpaProfileRepository {

  List<TpaProfileService.Entry> listAll();

  void save(@NonNull UUID playerId, @NonNull TpaProfile profile);
}
