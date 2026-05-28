package com.hanielcota.essentials.modules.chat;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.channel.GlobalChannel;
import com.hanielcota.essentials.modules.chat.channel.LocalChannel;
import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import com.hanielcota.essentials.modules.chat.command.ChatCommand;
import com.hanielcota.essentials.modules.chat.command.ChatNotifier;
import com.hanielcota.essentials.modules.chat.command.GlobalChatCommand;
import com.hanielcota.essentials.modules.chat.command.GlobalChatNotifier;
import com.hanielcota.essentials.modules.chat.command.LocalChannelNotifier;
import com.hanielcota.essentials.modules.chat.command.StaffChatCommand;
import com.hanielcota.essentials.modules.chat.command.StaffChatNotifier;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.format.ChatFormatPipeline;
import com.hanielcota.essentials.modules.chat.format.ChatLineRenderer;
import com.hanielcota.essentials.modules.chat.format.ChatTagResolverFactory;
import com.hanielcota.essentials.modules.chat.format.ChatTemplateCompiler;
import com.hanielcota.essentials.modules.chat.format.PlayerMessageStyler;
import com.hanielcota.essentials.modules.chat.guard.ChatGuardCheck;
import com.hanielcota.essentials.modules.chat.guard.ChatGuardPipeline;
import com.hanielcota.essentials.modules.chat.guard.CooldownCheck;
import com.hanielcota.essentials.modules.chat.guard.RepeatedMessageCheck;
import com.hanielcota.essentials.modules.chat.listener.AsyncChatListener;
import com.hanielcota.essentials.modules.chat.listener.ChatDispatchOrchestrator;
import com.hanielcota.essentials.modules.chat.listener.ChatPlayerCleanupListener;
import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderApiResolver;
import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderResolver;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.List;
import lombok.NonNull;

public final class ChatModule extends AbstractModule {

  public ChatModule() {
    super("chat");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("chat", ChatConfig.class, ChatConfig::defaults);
    var configs = env.service(ConfigService.class);
    var actors = env.service(ActorFactory.class);
    var audiences = env.service(AudienceProvider.class);
    var players = env.service(PlayerProvider.class);

    var placeholders =
        env.findService(PlaceholderResolver.class).orElseGet(PlaceholderApiResolver::new);

    var compiler = new ChatTemplateCompiler();
    var resolverFactory = new ChatTagResolverFactory(config, placeholders);
    var renderer = new ChatLineRenderer();
    var formatPipeline = new ChatFormatPipeline(compiler, placeholders, resolverFactory, renderer);
    var styler = new PlayerMessageStyler();

    var toggleService = new StaffChatToggleService();
    var cooldowns = new CooldownService();
    var antiSpam = new AntiSpamService();

    List<ChatGuardCheck> checks =
        List.of(new CooldownCheck(config, cooldowns), new RepeatedMessageCheck(config, antiSpam));
    var guards = new ChatGuardPipeline(checks);

    var globalChannel = new GlobalChannel();
    var localChannelNotifier = new LocalChannelNotifier(config);
    var localChannel = new LocalChannel(config, localChannelNotifier);
    var staffChannel = new StaffChannel();

    var router = new ChannelRouter(toggleService, localChannel, staffChannel);

    var chatNotifier = new ChatNotifier(config, actors);
    var staffNotifier = new StaffChatNotifier(config, formatPipeline, players, audiences);
    var globalNotifier =
        new GlobalChatNotifier(config, formatPipeline, styler, guards, globalChannel, audiences);

    registrar.command(new ChatCommand(configs, chatNotifier));
    registrar.command(new GlobalChatCommand(globalNotifier));
    registrar.command(new StaffChatCommand(toggleService, staffNotifier));

    var dispatchOrchestrator =
        new ChatDispatchOrchestrator(config, router, formatPipeline, guards, styler);
    registrar.listener(new AsyncChatListener(dispatchOrchestrator));
    registrar.listener(new ChatPlayerCleanupListener(toggleService, cooldowns, antiSpam));
  }
}
