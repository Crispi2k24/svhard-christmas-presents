package pl.svhard.christmas.presents.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.svhard.christmas.presents.event.AllPresentsCollectedEvent;
import pl.svhard.christmas.presents.notification.NoticeService;

public class PresentCompletionController implements Listener {

    private final NoticeService noticeService;

    public PresentCompletionController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @EventHandler
    public void onAllPresentsCompleted(AllPresentsCollectedEvent event) {
        this.noticeService.create()
            .notice(messages -> messages.completedAll)
            .viewer(event.getPlayer())
            .send();
    }
}
