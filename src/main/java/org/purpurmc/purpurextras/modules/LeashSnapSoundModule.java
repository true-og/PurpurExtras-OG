package org.purpurmc.purpurextras.modules;

import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.purpurmc.purpurextras.PurpurExtrasOG;

/**
 * Adds a sound for when the leash snaps
 */
public class LeashSnapSoundModule implements PurpurExtrasModule, Listener {

    private String sound;
    private double volume;
    private double pitch;

    protected LeashSnapSoundModule() {
        sound = PurpurExtrasOG.getPurpurConfig().getString("settings.leash-snap.sound", "minecraft:block.bamboo.break");
        volume = PurpurExtrasOG.getPurpurConfig().getDouble("settings.leash-snap.volume", 1f);
        pitch = PurpurExtrasOG.getPurpurConfig().getDouble("settings.leash-snap.pitch", 1.25f);
    }

    @Override
    public void enable() {
        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtrasOG.getPurpurConfig().getBoolean("settings.leash-snap.enabled", false) && sound != null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeashBreak(EntityUnleashEvent event){

        if (event.getReason() != EntityUnleashEvent.UnleashReason.DISTANCE) return;

        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), sound, SoundCategory.PLAYERS, (float) volume, (float) pitch);
    }
}
