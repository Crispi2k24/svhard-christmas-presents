package pl.svhard.christmas.presents.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.svhard.christmas.presents.present.PresentService;

public class PlayerConnectionController implements Listener {

    private final PresentService presentService;

    public PlayerConnectionController(PresentService presentService) {
        this.presentService = presentService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.presentService.loadSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.presentService.unloadSession(event.getPlayer().getUniqueId());
    }
}
