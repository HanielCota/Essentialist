package com.hanielcota.essentials.modules.homes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class SqlHomeMapperTest {

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

  private static ResultSet resultSet(String material) {
    var values =
        Map.<String, Object>of(
            "player_id",
            UUID.randomUUID().toString(),
            "name",
            "base",
            "world",
            "world",
            "x",
            1D,
            "y",
            2D,
            "z",
            3D,
            "yaw",
            4D,
            "pitch",
            5D,
            "material",
            material,
            "created_at",
            6L);

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
                case "toString" -> "HomeResultSet";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }
}
