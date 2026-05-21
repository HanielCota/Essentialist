package com.hanielcota.essentials.command.annotation;

import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation for Essentials player commands. Applies {@link PlayerOnly} so the command can
 * only be executed by players.
 *
 * <p>Add {@code @Permission("essentials.<command>")} alongside this annotation and
 * {@code @PermissionForOther("essentials.<command>.others")} when the command supports targeting
 * another player.
 */
@PlayerOnly
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EssentialsCommand {}
