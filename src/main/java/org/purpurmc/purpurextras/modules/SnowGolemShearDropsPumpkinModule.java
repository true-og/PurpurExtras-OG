package org.purpurmc.purpurextras.modules;

import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.purpurmc.purpurextras.PurpurExtrasOG;

/**
 * A module that disables the dropping of pumpkins when a Snow Golem is sheared.
 */
public class SnowGolemShearDropsPumpkinModule implements PurpurExtrasModule, Listener {

    @Override
    public void enable() {

        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public boolean shouldEnable() {

        return !PurpurExtrasOG.getPurpurConfig().getBoolean("settings.mobs.snow_golem.drop-pumpkin-when-sheared", true);

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSnowGolemShear(PlayerShearEntityEvent event) {

        if (!(event.getEntity() instanceof Snowman snowman))
            return;
        if (snowman.isDerp())
            return;

        // Remove default drops.
        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSnowGolemShear(BlockShearEntityEvent event) {

        if (!(event.getEntity() instanceof Snowman snowman))
            return;
        if (snowman.isDerp())
            return;

        // Remove default drops.
        event.setCancelled(true);

    }

}
