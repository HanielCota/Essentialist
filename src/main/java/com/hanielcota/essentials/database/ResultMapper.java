package com.hanielcota.essentials.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

/**
 * Functional interface for mapping a {@link ResultSet} row to a Java object.
 *
 * @param <T> the target type
 */
@FunctionalInterface
public interface ResultMapper<T> {

  T map(@NonNull ResultSet rs) throws SQLException;
}
