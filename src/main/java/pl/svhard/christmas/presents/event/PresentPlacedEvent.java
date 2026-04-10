package pl.svhard.christmas.presents.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.svhard.christmas.presents.present.Present;

public class PresentPlacedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Present present;

    public PresentPlacedEvent(Player player, Present present) {
        this.player = player;
        this.present = present;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Present getPresent() {
        return this.present;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
