package pl.svhard.christmas.presents.controller;

import com.eternalcode.commons.bukkit.position.Position;
import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.commons.scheduler.Scheduler;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.svhard.christmas.presents.event.AllPresentsCollectedEvent;
import pl.svhard.christmas.presents.event.PresentCollectedEvent;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentService;
import pl.svhard.christmas.presents.reward.CollectionResult;

public class PresentCollectionController implements Listener {

    private final PresentService presentService;
    private final NoticeService noticeService;
    private final Scheduler scheduler;

    public PresentCollectionController(
        PresentService presentService,
        NoticeService noticeService,
        Scheduler scheduler
    ) {
        this.presentService = presentService;
        this.noticeService = noticeService;
        this.scheduler = scheduler;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        this.handleCollection(event.getPlayer(), block, event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.handleCollection(event.getPlayer(), event.getBlock(), event);
    }

    private void handleCollection(Player player, Block block, Cancellable event) {
        Location blockLocation = block.getLocation();
        Position presentLocation = PositionAdapter.convert(blockLocation);

        Optional<Present> presentOpt = this.presentService.getPresentAtLocation(presentLocation);

        if (presentOpt.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        if (block.getType() == Material.AIR) {
            return;
        }

        if (!player.hasPermission("svhard.christmas.presents.use")) {
            this.noticeService.create()
                .notice(messages -> messages.noPermission)
                .viewer(player)
                .send();
            return;
        }

        Present present = presentOpt.get();

        this.presentService.collectPresentAsync(player.getUniqueId(), present.getId())
            .thenAccept(result -> {
                this.scheduler.run(() -> {
                    if (!result.isSuccess()) {
                        if (result.getStatus() == CollectionResult.CollectionStatus.ALREADY_COLLECTED) {
                            this.noticeService.create()
                                .notice(messages -> messages.alreadyCollected)
                                .viewer(player)
                                .send();
                        }
                        return;
                    }

                    PresentCollectedEvent collectionEvent =
                        new PresentCollectedEvent(player, present, result);
                    Bukkit.getPluginManager().callEvent(collectionEvent);

                    if (result.isCompletedAll()) {
                        AllPresentsCollectedEvent completedEvent =
                            new AllPresentsCollectedEvent(player, result.getTotalCount());
                        Bukkit.getPluginManager().callEvent(completedEvent);
                    }
                });
            });
    }
}
