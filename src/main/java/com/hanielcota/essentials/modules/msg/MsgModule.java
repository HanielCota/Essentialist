package com.hanielcota.essentials.modules.msg;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.msg.command.MsgCommand;
import com.hanielcota.essentials.modules.msg.command.MsgDispatcher;
import com.hanielcota.essentials.modules.msg.command.MsgNotifier;
import com.hanielcota.essentials.modules.msg.command.ReplyCommand;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.modules.msg.listener.MsgQuitListener;
import com.hanielcota.essentials.modules.msg.service.MsgService;
import com.hanielcota.essentials.modules.msg.service.SocialSpyBridge;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MsgModule extends AbstractModule {

  public MsgModule() {
    super("msg");
  }

  private static boolean canSee(
      VanishService vanish, @NonNull Player viewer, @NonNull Player target) {
    if (vanish == null) {
      return true;
    }
    if (viewer.hasPermission(VanishVisibilityApplier.SEE_PERMISSION)) {
      return true;
    }
    var targetId = target.getUniqueId();

    return !vanish.isVanished(targetId);
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var msgService = new MsgService();
    var config = registrar.configure("msg", MsgConfig.class, MsgConfig::defaults, msgService);
    var actors = env.service(ActorFactory.class);
    var players = env.service(PlayerProvider.class);
    var notifier = new MsgNotifier(config, actors);
    var spyBridge = new SocialSpyBridge(() -> env.findService(SocialSpyBroadcaster.class));
    var dispatcher = new MsgDispatcher(msgService, notifier, spyBridge);
    var visibilityFilter = visibilityFilter(env);

    registrar.command(new MsgCommand(config, dispatcher, visibilityFilter));
    registrar.command(new ReplyCommand(config, msgService, dispatcher, players, visibilityFilter));

    registrar.listener(new MsgQuitListener(msgService));
  }

  /**
   * Predicate that hides vanished targets from /msg / /r — unless the sender has {@link
   * VanishVisibilityApplier#SEE_PERMISSION}. Looks up {@link VanishService} on each call so module
   * load order between vanish and msg does not matter; when vanish is disabled every player is
   * visible.
   */
  private BiPredicate<Player, Player> visibilityFilter(@NonNull ModuleEnvironment env) {
    return (viewer, target) -> {
      var vanish = env.findService(VanishService.class).orElse(null);
      return canSee(vanish, viewer, target);
    };
  }
}
