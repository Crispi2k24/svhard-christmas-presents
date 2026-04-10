package pl.svhard.christmas.presents.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.reward.CollectionResult;

public class PresentCollectedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Present present;
    private final CollectionResult result;

    public PresentCollectedEvent(Player player, Present present, CollectionResult result) {
        this.player = player;
        this.present = present;
        this.result = result;
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

    public CollectionResult getResult() {
        return this.result;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
