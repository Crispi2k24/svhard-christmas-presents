package pl.svhard.christmas.presents.controller;

import java.util.Collection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.svhard.christmas.presents.event.PresentAddedEvent;
import pl.svhard.christmas.presents.event.PresentRemovedEvent;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentIdMapper;
import pl.svhard.christmas.presents.present.PresentService;
import pl.svhard.christmas.presents.present.animation.PresentIdleAnimationService;

public final class PresentDynamicUpdateController implements Listener {

    private final PresentIdleAnimationService animationService;
    private final PresentService presentService;
    private final PresentIdMapper idMapper;

    public PresentDynamicUpdateController(
        PresentIdleAnimationService animationService,
        PresentService presentService,
        PresentIdMapper idMapper
    ) {
        this.animationService = animationService;
        this.presentService = presentService;
        this.idMapper = idMapper;
    }

    @EventHandler
    public void onPresentAdd(PresentAddedEvent event) {
        this.updateAnimationsAndMapping();
    }

    @EventHandler
    public void onPresentRemove(PresentRemovedEvent event) {
        this.updateAnimationsAndMapping();
    }

    private void updateAnimationsAndMapping() {
        Collection<Present> presents =
            this.presentService.getAllPresents();

        this.animationService.startAll(presents);
        this.idMapper.updateMapping(presents);
    }
}
