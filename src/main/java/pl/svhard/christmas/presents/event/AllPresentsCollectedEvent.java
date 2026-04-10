package pl.svhard.christmas.presents.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AllPresentsCollectedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int totalPresents;

    public AllPresentsCollectedEvent(Player player, int totalPresents) {
        this.player = player;
        this.totalPresents = totalPresents;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getTotalPresents() {
        return this.totalPresents;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
