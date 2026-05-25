package com.hanielcota.essentials.modules.msg;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.msg.command.MsgCommand;
import com.hanielcota.essentials.modules.msg.command.ReplyCommand;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.modules.msg.listener.MsgQuitListener;
import com.hanielcota.essentials.modules.msg.service.MsgDispatcher;
import com.hanielcota.essentials.modules.msg.service.MsgNotifier;
import com.hanielcota.essentials.modules.msg.service.MsgService;
import com.hanielcota.essentials.modules.msg.service.SocialSpyBridge;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MsgModule extends AbstractModule {

  public MsgModule() {
    super("msg");
  }

  @Override
  protected void onEnable() {
    var config = configure("msg", MsgConfig.class, MsgConfig::defaults, new MsgService());
    var partners = service(MsgService.class);
    var framework = service(PaperCommandFramework.class);
    var players = service(PlayerProvider.class);
    var registry = context().services();
    var notifier = new MsgNotifier(config, framework);
    var spyBridge = new SocialSpyBridge(() -> registry.find(SocialSpyBroadcaster.class));
    var dispatcher = new MsgDispatcher(partners, notifier, spyBridge);
    var visibilityFilter = visibilityFilter();

    registerCommand(new MsgCommand(config, dispatcher, visibilityFilter));
    registerCommand(new ReplyCommand(config, partners, dispatcher, players, visibilityFilter));

    registerListener(new MsgQuitListener(partners));
  }

  /**
   * Predicate that hides vanished targets from /msg / /r — unless the sender has {@link
   * VanishVisibilityApplier#SEE_PERMISSION}. Looks up {@link VanishService} on each call so module
   * load order between vanish and msg does not matter; when vanish is disabled every player is
   * visible.
   */
  private BiPredicate<Player, Player> visibilityFilter() {
    var registry = context().services();
    return (viewer, target) -> {
      var vanish = registry.find(VanishService.class).orElse(null);
      return canSee(vanish, viewer, target);
    };
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
}
