package pl.svhard.christmas.presents.controller;

import com.eternalcode.commons.bukkit.position.Position;
import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.commons.scheduler.Scheduler;
import com.eternalcode.multification.notice.Notice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.svhard.christmas.presents.event.PresentPlacedEvent;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.PresentItemFactory;
import pl.svhard.christmas.presents.present.PresentService;

public class PresentPlacementController implements Listener {

    private final PresentService presentService;
    private final PresentItemFactory itemFactory;
    private final NoticeService noticeService;
    private final Scheduler scheduler;

    public PresentPlacementController(
        PresentService presentService,
        PresentItemFactory itemFactory,
        NoticeService noticeService, Scheduler scheduler
    ) {
        this.presentService = presentService;
        this.itemFactory = itemFactory;
        this.noticeService = noticeService;
        this.scheduler = scheduler;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.itemFactory.isPresentHead(event.getItemInHand())) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("svhard.christmas.presents.give")) {
            event.setCancelled(true);
            this.noticeService.create()
                .notice(messages -> messages.noPermission)
                .viewer(player)
                .send();
            return;
        }

        Location blockLocation = event.getBlockPlaced().getLocation();
        Position presentLocation = PositionAdapter.convert(blockLocation);

        if (this.presentService.getPresentAtLocation(presentLocation).isPresent()) {
            event.setCancelled(true);
            this.noticeService.create()
                .notice(messages -> messages.presentAlreadyExists)
                .viewer(player)
                .send();
            return;
        }

        this.presentService.placePresentAsync(presentLocation).thenAccept(present -> {
            this.noticeService.create()
                .notice(messages -> messages.presentPlaced)
                .placeholder("{ID}", present.getId().toString())
                .viewer(player)
                .send();

            this.scheduler.run(() -> {
                PresentPlacedEvent presentPlacedEvent = new PresentPlacedEvent(player, present);
                Bukkit.getPluginManager().callEvent(presentPlacedEvent);
            });
        }).exceptionally(throwable -> {
            this.noticeService.create()
                .notice(messages -> Notice.chat("An error occurred while saving the present."))
                .viewer(player)
                .send();
            throwable.printStackTrace();
            return null;
        });
    }
}
