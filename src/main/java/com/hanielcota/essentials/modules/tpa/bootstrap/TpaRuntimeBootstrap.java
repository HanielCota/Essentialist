package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoriteNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.command.TpaIncomingResolver;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteChatListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteSessionCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSessions;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaRuntimeBootstrap {

  private final @NonNull ModuleEnvironment env;
  private final @NonNull ModuleRegistrar registrar;
  private final @NonNull ConfigHandle<TpaConfig> config;

  public TpaRuntime requestRuntime(
      @NonNull AsyncTpaHistory history,
      @NonNull TpaProfileService profiles,
      @NonNull TpaBlockService blocks,
      @NonNull TpaContactService contacts) {
    var store = new RequestRepository();
    var players = this.env.service(PlayerProvider.class);
    var notifier = new TpaNotifier(this.config, players, profiles);
    var requestService =
        new TeleportRequestService(
            this.config, store, history, notifier, players, profiles, blocks, contacts);

    var scheduler = this.env.service(Scheduler.class);
    var expiry = new TeleportRequestExpiry(scheduler, store, requestService);
    expiry.start();
    this.registrar.closeable(expiry::stop);

    return new TpaRuntime(requestService, notifier);
  }

  public TpaShared sharedHelpers(@NonNull TeleportRequestService requestService) {
    var actors = this.env.service(ActorFactory.class);
    var players = this.env.service(PlayerProvider.class);
    var callbacks = this.env.service(MainThreadCallbacks.class);
    var replyNotifier = new TpaRequestReplyNotifier(actors, players);
    var acceptHandler = new TpAcceptResultHandler(this.config, replyNotifier);
    var incomingResolver = new TpaIncomingResolver(this.config, requestService);

    return new TpaShared(
        actors, players, callbacks, replyNotifier, acceptHandler, incomingResolver);
  }

  public FavoriteRuntime favoriteRuntime(
      @NonNull TpaFavoriteService favorites, @NonNull TpaProfileService profiles) {
    var sessions = new TpaFavoriteSessions();
    var selections = new TpaFavoriteSelections();
    var notifier = new TpaFavoriteNotifier(this.config);
    var players = this.env.service(PlayerProvider.class);
    var scheduler = this.env.service(Scheduler.class);
    var orchestrator =
        new TpaFavoritePromptOrchestrator(
            this.config, favorites, sessions, notifier, players, scheduler, profiles);

    var chatListener = new TpaFavoriteChatListener(orchestrator, sessions);
    this.registrar.listener(chatListener);

    var cleanupListener = new TpaFavoriteSessionCleanupListener(sessions, selections);
    this.registrar.listener(cleanupListener);

    return new FavoriteRuntime(selections, orchestrator);
  }

  public TpaSendOrchestrator sendDispatcher(
      @NonNull TeleportRequestService requests,
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaProfileService profiles,
      @NonNull TpaShared shared) {
    return new TpaSendOrchestrator(
        this.config,
        requests,
        favorites,
        profiles,
        shared.acceptHandler(),
        shared.callbacks(),
        shared.actors());
  }

  public void registerQuitListener(
      @NonNull TpaRuntime runtime, @NonNull TpaPendingSelections pendingSelections) {
    var requestService = runtime.requestService();
    var notifier = runtime.notifier();
    var quitListener = new TpaQuitListener(requestService, notifier, pendingSelections);

    this.registrar.listener(quitListener);
  }

  public record TpaRuntime(TeleportRequestService requestService, TpaNotifier notifier) {}

  public record FavoriteRuntime(
      TpaFavoriteSelections selections, TpaFavoritePromptOrchestrator orchestrator) {}

  public record TpaShared(
      ActorFactory actors,
      PlayerProvider players,
      MainThreadCallbacks callbacks,
      TpaRequestReplyNotifier replyNotifier,
      TpAcceptResultHandler acceptHandler,
      TpaIncomingResolver incomingResolver) {}
}
