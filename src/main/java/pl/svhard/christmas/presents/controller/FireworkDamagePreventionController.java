package pl.svhard.christmas.presents.controller;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class FireworkDamagePreventionController implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.FIREWORK_ROCKET) {
            return;
        }

        Firework firework = (Firework) event.getDamager();

        if (!firework.isPersistent()) {
            event.setCancelled(true);
        }
    }
}
