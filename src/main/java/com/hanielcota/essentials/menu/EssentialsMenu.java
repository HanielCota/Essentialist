package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import lombok.NonNull;

public interface EssentialsMenu {

  @NonNull String id();

  void register(@NonNull MenuService menus);
}
