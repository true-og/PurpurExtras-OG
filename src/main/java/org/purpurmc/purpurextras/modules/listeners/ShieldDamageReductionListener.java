package org.purpurmc.purpurextras.modules.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ShieldDamageReductionListener implements Listener {

	private final double shieldDamageReduction;

	public ShieldDamageReductionListener(double reductionModifier) {
		this.shieldDamageReduction = reductionModifier;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onShieldHit(EntityDamageByEntityEvent event) {
		if (! (event.getEntity() instanceof HumanEntity humanEntity)) return;
		if (! humanEntity.isBlocking()) return;

		double originalDamage = event.getDamage();
		double finalDamage = event.getFinalDamage();
		double reduction = originalDamage - finalDamage;

		double newReduction = shieldDamageReduction * reduction;
		double newDamage = finalDamage + newReduction;

		event.setDamage(newDamage);
	}

}