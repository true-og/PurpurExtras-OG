package org.purpurmc.purpurextras.modules.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShieldDamageReductionListener implements Listener {

    private final double shieldDamageReduction;

    public ShieldDamageReductionListener(double reductionModifier) {

        this.shieldDamageReduction = reductionModifier;

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShieldHit(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof HumanEntity humanEntity))
            return;
        if (!humanEntity.isBlocking())
            return;

        // Scale only the blocking modifier. Writing a post-armour value
        // into BASE made armour and resistance apply a second time.
        if (!event.isApplicable(EntityDamageEvent.DamageModifier.BLOCKING))
            return;

        double blocking = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        if (blocking == 0.0)
            return;

        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, blocking * shieldDamageReduction);

    }

}
