package com.hanielcota.essentials.modules.spawn.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line {@code /spawn} and {@code /setspawn} can send. */
@ConfigSerializable
public record SpawnMessages(
    @Comment("/setspawn confirmation.") String spawnSet,
    @Comment("Shown when /spawn runs and no spawn has been configured yet.") String noSpawn,
    @Comment("Shown when the configured spawn world is no longer loaded.") String worldGone,
    @Comment("Shown on /spawn start when a delay is configured. Placeholders: {seconds}.")
        String teleporting,
    @Comment("Shown after /spawn completes successfully.") String teleported,
    @Comment("Shown when the delayed teleport is cancelled by movement or damage.")
        String cancelled,
    @Comment("Shown when the teleport itself fails.") String failed) {

  public static SpawnMessages defaults() {
    return new SpawnMessages(
        "<green>Spawn definido para a sua localização atual.",
        "<red>O spawn ainda não foi configurado.",
        "<red>O mundo do spawn não está carregado.",
        "<yellow>Teleportando em <gold>{seconds}s</gold>. Não se mova nem tome dano.",
        "<green>Você foi teleportado para o spawn.",
        "<red>Teleporte cancelado.",
        "<red>O teleporte não pôde ser concluído.");
  }
}
