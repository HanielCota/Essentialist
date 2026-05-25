package com.hanielcota.essentials.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type or member as plugin-internal. External callers (addons, scripts) must not depend on
 * anything annotated this way — it can change shape, semantics or disappear in any release without
 * a deprecation cycle. Stable surface lives behind the typed {@code XxxApi} interfaces exposed by
 * {@link EssentialsApi}.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Internal {}
