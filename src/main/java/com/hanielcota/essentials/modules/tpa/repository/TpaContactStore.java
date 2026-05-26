package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import java.util.List;
import lombok.NonNull;

public interface TpaContactStore {

  List<TpaContact> listAll();

  void save(@NonNull TpaContact contact);
}
