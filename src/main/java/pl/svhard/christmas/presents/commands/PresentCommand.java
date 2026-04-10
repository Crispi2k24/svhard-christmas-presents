package pl.svhard.christmas.presents.commands;

import com.eternalcode.commons.bukkit.position.Position;
import com.eternalcode.commons.scheduler.Scheduler;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.svhard.christmas.presents.config.ConfigService;
import pl.svhard.christmas.presents.config.PresentMessages;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentIdMapper;
import pl.svhard.christmas.presents.present.PresentItemFactory;
import pl.svhard.christmas.presents.present.PresentService;

@Command(name = "prezent", aliases = {"present", "gift"})
public class PresentCommand {

    private final PresentService presentService;
    private final PresentIdMapper idMapper;
    private final PresentItemFactory itemFactory;
    private final PresentMessages messageConfig;
    private final Server server;
    private final ConfigService configService;
    private final NoticeService noticeService;
    private final MiniMessage miniMessage;
    private final Scheduler scheduler;

    public PresentCommand(
        PresentService presentService,
        PresentIdMapper idMapper,
        PresentItemFactory itemFactory,
        PresentMessages messageConfig,
        Server server,
        ConfigService configService,
        NoticeService noticeService,
        MiniMessage miniMessage,
        Scheduler scheduler) {
        this.presentService = presentService;
        this.idMapper = idMapper;
        this.itemFactory = itemFactory;
        this.messageConfig = messageConfig;
        this.server = server;
        this.configService = configService;
        this.noticeService = noticeService;
        this.miniMessage = miniMessage;
        this.scheduler = scheduler;
    }

    @Execute
    @Permission("svhard.christmas.presents.use")
    void help(@Context CommandSender sender) {
        this.noticeService.create()
            .notice(messages -> messages.helpMessage)
            .viewer(sender)
            .send();
    }

    @Execute(name = "give")
    @Permission("svhard.christmas.presents.give")
    CompletableFuture<Void> give(@Context Player sender, @OptionalArg Player target) {
        Player receiver = target != null ? target : sender;

        return this.itemFactory.createPresentHead().thenAccept(presentHead -> {
            this.server.getScheduler().runTask(
                JavaPlugin.getProvidingPlugin(PresentCommand.class),
                () -> {
                    receiver.getInventory().addItem(presentHead);

                    this.noticeService.create()
                        .notice(messages -> messages.presentGivenReceiver)
                        .viewer(receiver)
                        .send();

                    if (!sender.equals(receiver)) {
                        this.noticeService.create()
                            .notice(messages -> messages.presentGiven)
                            .placeholder("{PLAYER}", receiver.getName())
                            .viewer(sender)
                            .send();
                    }
                });
        });
    }

    @Execute(name = "remove")
    @Permission("svhard.christmas.presents.admin")
    CompletableFuture<Void> remove(@Context CommandSender sender, @Arg Present present) {
        int numericId = this.idMapper.getNumericId(present.getId()).orElse(-1);
        String displayId = numericId > 0 ? String.valueOf(numericId) : present.getId().toString();

        // Execute asynchronously to avoid PresentRemovedEvent IllegalStateException
        CompletableFuture<Void> future = new CompletableFuture<>();

        this.scheduler.runAsync(() -> {
            this.presentService.removePresentAsync(present.getId()).thenAccept(v -> {
                this.noticeService.create()
                    .notice(messages -> messages.presentRemoved)
                    .placeholder("{ID}", displayId)
                    .viewer(sender)
                    .send();
                future.complete(null);
            }).exceptionally(throwable -> {
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return future;
    }

    @Execute(name = "list")
    @Permission("svhard.christmas.presents.admin")
    void list(@Context Player sender) {
        List<Present> presents = this.idMapper.getAllPresentsSortedByNumericId();

        if (presents.isEmpty()) {
            this.noticeService.create()
                .notice(messages -> messages.presentListEmpty)
                .viewer(sender)
                .send();
            return;
        }

        String headerText = this.messageConfig.presentListHeader.replace("{COUNT}", String.valueOf(presents.size()));
        Component headerComponent = this.miniMessage.deserialize(headerText);
        sender.sendMessage(headerComponent);

        for (Present present : presents) {
            int numericId = this.idMapper.getNumericId(present.getId()).orElse(-1);
            String displayId = numericId > 0 ? String.valueOf(numericId) : present.getId().toString();

            Position position = present.getLocation();

            String entryText = this.messageConfig.presentListEntry
                .replace("{ID}", displayId)
                .replace("{WORLD}", position.world())
                .replace("{X}", String.format("%.1f", position.x()))
                .replace("{Y}", String.format("%.1f", position.y()))
                .replace("{Z}", String.format("%.1f", position.z()));

            Component entryComponent = this.miniMessage.deserialize(entryText);
            sender.sendMessage(entryComponent);
        }
    }

    @Execute(name = "reload")
    @Permission("svhard.christmas.presents.admin")
    void reload(@Context CommandSender sender) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        this.configService.reload();

        Stopwatch stop = stopwatch.stop();
        this.noticeService.create()
            .notice(messages -> messages.reloaded)
            .viewer(sender)
            .placeholder("{TIME}", String.valueOf(stop.elapsed().toMillis()))
            .send();
    }
}
