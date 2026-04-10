package pl.svhard.christmas.presents.controller;

import com.eternalcode.commons.bukkit.position.Position;
import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.commons.scheduler.Scheduler;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentService;

public final class PresentAdminRemovalController implements Listener {

    private static final long CONFIRMATION_TIMEOUT_MS = 5000; // 5 seconds

    private final PresentService presentService;
    private final NoticeService noticeService;
    private final Scheduler scheduler;

    private final Map<UUID, PendingRemoval> pendingRemovals = new ConcurrentHashMap<>();

    public PresentAdminRemovalController(
        PresentService presentService,
        NoticeService noticeService,
        Scheduler scheduler
    ) {
        this.presentService = presentService;
        this.noticeService = noticeService;
        this.scheduler = scheduler;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return;
        }

        if (!player.hasPermission("svhard.christmas.presents.admin")) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Location blockLocation = clickedBlock.getLocation();
        Position presentLocation = PositionAdapter.convert(blockLocation);

        Optional<Present> presentOpt = this.presentService.getPresentAtLocation(presentLocation);

        if (presentOpt.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        Present present = presentOpt.get();
        UUID playerUuid = player.getUniqueId();

        PendingRemoval pending = this.pendingRemovals.get(playerUuid);

        if (pending != null && pending.isSameLocation(presentLocation) && !pending.isExpired()) {
            this.handleConfirmedRemoval(player, present, presentLocation);
        }
        else {
            this.handleFirstClick(player, present, presentLocation);
        }
    }

    private void handleFirstClick(Player player, Present present, Position location) {
        UUID playerUuid = player.getUniqueId();

        this.pendingRemovals.put(playerUuid, new PendingRemoval(location, System.currentTimeMillis()));

        this.noticeService.create()
            .notice(messages -> messages.confirmRemoval)
            .viewer(player)
            .send();

        this.scheduler.runLater(
            () -> {
                PendingRemoval current = this.pendingRemovals.get(playerUuid);
                if (current != null && current.isSameLocation(location)) {
                    this.pendingRemovals.remove(playerUuid);
                }
            }, Duration.ofMillis(CONFIRMATION_TIMEOUT_MS));
    }

    private void handleConfirmedRemoval(Player player, Present present, Position location) {
        UUID playerUuid = player.getUniqueId();

        this.pendingRemovals.remove(playerUuid);

        this.scheduler.runAsync(() -> {
            this.presentService.removePresentAsync(present.getId()).thenAccept(v -> {
                this.scheduler.run(() -> {
                    this.noticeService.create()
                        .notice(messages -> messages.presentRemovedByInteraction)
                        .viewer(player)
                        .send();
                });
            });
        });
    }

    private record PendingRemoval(Position location, long timestamp) {

        boolean isSameLocation(Position other) {
            return this.location.equals(other);
        }

        boolean isExpired() {
            return System.currentTimeMillis() - this.timestamp > CONFIRMATION_TIMEOUT_MS;
        }
    }
}
