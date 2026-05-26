package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface NickStore {

  List<NickEntry> list();

  void save(@NonNull NickEntry entry);

  boolean delete(@NonNull UUID id);
}
