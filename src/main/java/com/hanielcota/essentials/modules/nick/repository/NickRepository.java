package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

/**
 * Read+write surface for nicknames. The SQL implementation answers every query straight from the
 * database; the cached implementation answers lookups from a {@link NickCache} populated at boot
 * and submits persistence to the async writer.
 */
public interface NickRepository {

  List<NickEntry> list();

  Optional<NickEntry> findById(@NonNull UUID id);

  Optional<UUID> idByNickname(@NonNull String nickname);

  boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self);

  void save(@NonNull NickEntry entry);

  boolean delete(@NonNull UUID id);
}
