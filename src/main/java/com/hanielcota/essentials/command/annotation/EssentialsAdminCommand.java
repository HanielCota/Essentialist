package com.hanielcota.essentials.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation for Essentials admin commands (console or player). Has no built-in behaviour —
 * use it for documentation and consistency.
 *
 * <p>Add {@code @Permission("essentials.<command>")} when permission is required.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EssentialsAdminCommand {}
