package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SqlHomeMapper {

  static Home read(@NonNull ResultSet rs) throws SQLException {
    var ownerId = UUID.fromString(rs.getString("player_id"));
    var name = rs.getString("name");
    var world = rs.getString("world");

    var x = rs.getDouble("x");
    var y = rs.getDouble("y");
    var z = rs.getDouble("z");

    var yaw = (float) rs.getDouble("yaw");
    var pitch = (float) rs.getDouble("pitch");

    var material = parseMaterial(rs.getString("material"));
    var createdAt = rs.getLong("created_at");

    return new Home(ownerId, name, world, x, y, z, yaw, pitch, material, createdAt);
  }

  private static Material parseMaterial(String name) {
    if (name == null || name.isBlank()) {
      return Material.RED_BED;
    }

    var material = Material.matchMaterial(name);
    if (material != null) {
      return material;
    }

    return Material.RED_BED;
  }
}
