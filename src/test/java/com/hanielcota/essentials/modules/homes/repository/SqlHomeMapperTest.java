package com.hanielcota.essentials.modules.homes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class SqlHomeMapperTest {

  private static ResultSet resultSet(String material) {
    var values =
        Map.<String, Object>ofEntries(
            Map.entry("player_id", UUID.randomUUID().toString()),
            Map.entry("name", "base"),
            Map.entry("world", "world"),
            Map.entry("x", 1D),
            Map.entry("y", 2D),
            Map.entry("z", 3D),
            Map.entry("yaw", 4D),
            Map.entry("pitch", 5D),
            Map.entry("material", material),
            Map.entry("created_at", 6L),
            Map.entry("pinned", 0));

    return (ResultSet)
        Proxy.newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class<?>[] {ResultSet.class},
            (ignored, method, args) -> {
              var column = args == null || args.length == 0 ? null : String.valueOf(args[0]);
              return switch (method.getName()) {
                case "getString" -> String.valueOf(values.get(column));
                case "getDouble" -> ((Number) values.get(column)).doubleValue();
                case "getLong" -> ((Number) values.get(column)).longValue();
                case "getInt" -> ((Number) values.get(column)).intValue();
                case "toString" -> "HomeResultSet";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void fallsBackToDefaultIconWhenPersistedMaterialIsNotRenderable() throws Exception {
    var home = SqlHomeMapper.read(resultSet("AIR"));

    assertEquals(Material.RED_BED, home.material());
  }

  @Test
  void fallsBackToDefaultIconWhenPersistedMaterialIsUnknown() throws Exception {
    var home = SqlHomeMapper.read(resultSet("not_a_material"));

    assertEquals(Material.RED_BED, home.material());
  }
}
