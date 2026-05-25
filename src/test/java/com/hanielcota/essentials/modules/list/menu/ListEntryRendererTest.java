package com.hanielcota.essentials.modules.list.menu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.model.PlayerEntry;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.junit.jupiter.api.Test;

class ListEntryRendererTest {

  @Test
  void hidesVanillaItemAttributesFromPlayerEntries() {
    var renderer = new ListEntryRenderer(config(ListConfig.defaults()));
    var entry =
        new PlayerEntry(
            UUID.randomUUID(), "Haniel", "admin", "<gold>Admin", Material.DIAMOND_HELMET, 100);

    var template = renderer.render(entry);

    assertTrue(Arrays.asList(template.flags()).contains(ItemFlag.HIDE_ATTRIBUTES));
  }

  private static ConfigHandle<ListConfig> config(ListConfig value) {
    return new ConfigHandle<>() {
      @Override
      public String name() {
        return "list";
      }

      @Override
      public ListConfig value() {
        return value;
      }

      @Override
      public void reload() {
        // no-op — test double, the renderer never reloads during the test
      }
    };
  }
}
