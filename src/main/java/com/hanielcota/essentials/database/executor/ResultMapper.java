package com.hanielcota.essentials.database.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

@FunctionalInterface
public interface ResultMapper<T> {

  T map(@NonNull ResultSet rs) throws SQLException;
}
