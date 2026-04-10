package pl.svhard.christmas.presents.controller;

import com.eternalcode.commons.bukkit.position.PositionAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.svhard.christmas.presents.config.PresentMessages;
import pl.svhard.christmas.presents.event.PresentCollectedEvent;
import pl.svhard.christmas.presents.notification.NoticeService;
import pl.svhard.christmas.presents.present.PresentEffectsService;

public class PresentCollectionEffectsController implements Listener {

    private final PresentEffectsService effectsService;
    private final PresentMessages messageConfig;
    private final NoticeService noticeService;

    public PresentCollectionEffectsController(
        PresentEffectsService effectsService,
        PresentMessages messageConfig,
        NoticeService noticeService
    ) {
        this.effectsService = effectsService;
        this.messageConfig = messageConfig;
        this.noticeService = noticeService;
    }

    @EventHandler
    public void onPresentCollected(PresentCollectedEvent event) {
        Player player = event.getPlayer();

        this.noticeService.create()
            .notice(this.messageConfig.actionBarFormat)
            .placeholder("{COLLECTED}", String.valueOf(event.getResult().getCollectedCount()))
            .placeholder("{TOTAL}", String.valueOf(event.getResult().getTotalCount()))
            .viewer(player)
            .send();

        this.effectsService.spawnCollectionFirework(
            PositionAdapter.convert(event.getPresent().getLocation())
        );
    }
}
