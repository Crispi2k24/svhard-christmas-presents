package pl.svhard.christmas.presents.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.svhard.christmas.presents.present.Present;

public class PresentAddedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Present present;

    public PresentAddedEvent(Present present) {
        super(true);
        this.present = present;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Present getPresent() {
        return this.present;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
