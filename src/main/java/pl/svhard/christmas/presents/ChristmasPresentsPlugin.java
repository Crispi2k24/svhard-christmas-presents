package pl.svhard.christmas.presents;

import com.eternalcode.commons.bukkit.scheduler.BukkitSchedulerImpl;
import com.eternalcode.commons.scheduler.Scheduler;
import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticeBroadcast;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import java.io.File;
import java.util.Collection;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.svhard.christmas.presents.commands.PresentCommand;
import pl.svhard.christmas.presents.commands.argument.PresentArgument;
import pl.svhard.christmas.presents.commands.handler.InvalidUsageHandlerImpl;
import pl.svhard.christmas.presents.commands.handler.MissingPermissionHandlerImpl;
import pl.svhard.christmas.presents.commands.handler.NoticeBroadcastHandler;
import pl.svhard.christmas.presents.commands.handler.NoticeHandler;
import pl.svhard.christmas.presents.config.ConfigService;
import pl.svhard.christmas.presents.config.PluginConfig;
import pl.svhard.christmas.presents.config.PresentMessages;
import pl.svhard.christmas.presents.controller.FireworkDamagePreventionController;
import pl.svhard.christmas.presents.controller.PlayerConnectionController;
import pl.svhard.christmas.presents.controller.PresentAdminRemovalController;
import pl.svhard.christmas.presents.controller.PresentCollectionController;
import pl.svhard.christmas.presents.controller.PresentCollectionEffectsController;
import pl.svhard.christmas.presents.controller.PresentCompletionController;
import pl.svhard.christmas.presents.controller.PresentDynamicUpdateController;
import pl.svhard.christmas.presents.controller.PresentPlacementController;
import pl.svhard.christmas.presents.controller.PresentRewardController;
import pl.svhard.christmas.presents.database.DatabaseConfig;
import pl.svhard.christmas.presents.database.DatabaseManager;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.MissingPresentBlockRestorer;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentEffectsService;
import pl.svhard.christmas.presents.present.PresentIdMapper;
import pl.svhard.christmas.presents.present.PresentIdMapperImpl;
import pl.svhard.christmas.presents.present.PresentItemFactory;
import pl.svhard.christmas.presents.present.PresentRepository;
import pl.svhard.christmas.presents.present.PresentRepositoryImpl;
import pl.svhard.christmas.presents.present.PresentService;
import pl.svhard.christmas.presents.present.animation.PresentIdleAnimationService;
import pl.svhard.christmas.presents.progress.PlayerProgressRepository;
import pl.svhard.christmas.presents.progress.PlayerProgressRepositoryImpl;
import pl.svhard.christmas.presents.reward.RewardExecutor;

public class ChristmasPresentsPlugin extends JavaPlugin {

    private static final String CONFIG_FILE = "config.yml";
    private static final String MESSAGES_FILE = "messages.yml";
    private static final String DATABASE_FILE = "database.yml";

    private DatabaseManager databaseManager;
    private PresentIdleAnimationService animationService;
    private ConfigService configService;
    private PluginConfig config;
    private PresentMessages messageConfig;
    private PresentService presentService;
    private LiteCommands<?> liteCommands;

    @Override
    public void onEnable() {
        this.configService = new ConfigService();
        this.config = this.configService.create(
            PluginConfig.class,
            new File(this.getDataFolder(), CONFIG_FILE));
        this.messageConfig = this.configService.create(
            PresentMessages.class,
            new File(this.getDataFolder(), MESSAGES_FILE));
        DatabaseConfig databaseConfig = this.configService.create(
            DatabaseConfig.class,
            new File(this.getDataFolder(), DATABASE_FILE));

        MiniMessage miniMessage = MiniMessage.miniMessage();
        AudienceProvider audienceProvider = BukkitAudiences.create(this);
        Scheduler scheduler = new BukkitSchedulerImpl(this);

        this.databaseManager = new DatabaseManager(this.getLogger(), this.getDataFolder(), databaseConfig);
        this.databaseManager.connect();

        PresentRepository presentRepository = new PresentRepositoryImpl(this.databaseManager, scheduler);
        PlayerProgressRepository progressRepository = new PlayerProgressRepositoryImpl(this.databaseManager, scheduler);

        this.presentService = new PresentService(
            presentRepository, progressRepository,
            this.getServer().getPluginManager());
        NoticeService noticeService = new NoticeService(audienceProvider, this.messageConfig, miniMessage);

        PresentItemFactory itemFactory = new PresentItemFactory(
            miniMessage,
            this.config.presentItem.texture,
            this.config.presentItem.displayName);
        RewardExecutor rewardExecutor = new RewardExecutor(this.getServer());
        PresentEffectsService effectsService = new PresentEffectsService(this.config);
        PresentIdMapper idMapper = new PresentIdMapperImpl();

        this.animationService = new PresentIdleAnimationService(
            this.getServer(),
            scheduler,
            new PresentIdleAnimationService.AnimationConfig(
                this.config.animation.enabled,
                this.config.animation.getParticleType(),
                this.config.animation.particleCount,
                this.config.animation.particleSpeed,
                this.config.animation.orbitRadius,
                this.config.animation.rotationSpeed,
                this.config.animation.tickInterval,
                this.config.animation.viewDistance),
            this.presentService);

        this.presentService.loadPresents().thenAccept(v -> {
            Collection<Present> presents = this.presentService.getAllPresents();

            idMapper.updateMapping(presents);

            this.animationService.startAll(presents);
            this.getLogger().info("Loaded and started animation for " + presents.size() + " presents.");

            MissingPresentBlockRestorer blockRestorer = new MissingPresentBlockRestorer(scheduler, this.getLogger());
            itemFactory.createPresentHead().thenAccept(headItem -> {
                SkullMeta headMeta = (SkullMeta) headItem.getItemMeta();
                blockRestorer.restoreMissingBlocks(presents, headMeta);
            });
        });

        PresentPlacementController placementListener = new PresentPlacementController(
            this.presentService,
            itemFactory,
            noticeService,
            scheduler);
        this.getServer().getPluginManager().registerEvents(placementListener, this);

        PresentCollectionController collectionListener = new PresentCollectionController(
            this.presentService,
            noticeService,
            scheduler);
        this.getServer().getPluginManager().registerEvents(collectionListener, this);

        PresentCollectionEffectsController effectsListener = new PresentCollectionEffectsController(
            effectsService,
            this.messageConfig,
            noticeService);
        this.getServer().getPluginManager().registerEvents(effectsListener, this);

        PresentRewardController rewardListener = new PresentRewardController(rewardExecutor, this.config);
        this.getServer().getPluginManager().registerEvents(rewardListener, this);

        PresentCompletionController completionListener = new PresentCompletionController(noticeService);
        this.getServer().getPluginManager().registerEvents(completionListener, this);

        PresentDynamicUpdateController updateController = new PresentDynamicUpdateController(
            this.animationService,
            this.presentService,
            idMapper);
        this.getServer().getPluginManager().registerEvents(updateController, this);

        PresentAdminRemovalController adminRemovalController = new PresentAdminRemovalController(
            this.presentService,
            noticeService,
            scheduler);
        this.getServer().getPluginManager().registerEvents(adminRemovalController, this);

        FireworkDamagePreventionController fireworkDamageController = new FireworkDamagePreventionController();
        this.getServer().getPluginManager().registerEvents(fireworkDamageController, this);

        PlayerConnectionController connectionController = new PlayerConnectionController(this.presentService);
        this.getServer().getPluginManager().registerEvents(connectionController, this);

        PresentCommand presentCommand = new PresentCommand(
            this.presentService,
            idMapper,
            itemFactory,
            this.messageConfig,
            this.getServer(),
            this.configService,
            noticeService,
            miniMessage,
            scheduler);

        this.liteCommands = LiteBukkitFactory.builder("christmas-presents", this)
            .commands(presentCommand)
            .argument(Present.class, new PresentArgument(this.presentService, idMapper))
            .result(Notice.class, new NoticeHandler(noticeService))
            .result(NoticeBroadcast.class, new NoticeBroadcastHandler())
            .invalidUsage(new InvalidUsageHandlerImpl(noticeService))
            .missingPermission(new MissingPermissionHandlerImpl(noticeService))
            .build();
    }

    @Override
    public void onDisable() {
        if (this.animationService != null) {
            this.animationService.stopAll();
        }

        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }

        if (this.databaseManager != null) {
            this.databaseManager.close();
        }
    }
}
