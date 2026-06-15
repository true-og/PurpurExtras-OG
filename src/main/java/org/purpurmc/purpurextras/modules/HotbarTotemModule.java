package org.purpurmc.purpurextras.modules;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.PurpurExtrasOG;

/**
 * If enabled, a totem of undying will save a player from death while it sits
 * anywhere in their hotbar, not only when held in the main hand or off hand.
 * Vanilla resurrection (totem in either hand) is left untouched; this module
 * only covers the remaining hotbar slots. Void deaths are intentionally ignored
 * because surviving them requires the teleport handled by
 * {@link VoidTotemModule}.
 */
public class HotbarTotemModule implements PurpurExtrasModule, Listener {

    private final Collection<PotionEffect> totemEffects = new ArrayList<>();

    protected HotbarTotemModule() {

        totemEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 45, 1));
        totemEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 40, 0));
        totemEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 5, 1));

    }

    @Override
    public void enable() {

        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public boolean shouldEnable() {

        return PurpurExtrasOG.getPurpurConfig().getBoolean("settings.totem.work-from-hotbar", false);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLethalDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player))
            return;
        // Void deaths are handled by VoidTotemModule, which teleports the player to
        // safety.
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID))
            return;
        // Damage is survivable, let it through unchanged.
        if (player.getHealth() - event.getFinalDamage() > 0)
            return;

        PlayerInventory inventory = player.getInventory();
        // Vanilla already resurrects when a totem is held in either hand.
        if (inventory.getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING)
                || inventory.getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING))
            return;

        int slot = findHotbarTotemSlot(inventory);
        if (slot < 0)
            return;

        event.setCancelled(true);
        consumeTotem(inventory, slot);
        applyTotemEffects(player);

    }

    private int findHotbarTotemSlot(PlayerInventory inventory) {

        for (int slot = 0; slot <= 8; slot++) {

            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType().equals(Material.TOTEM_OF_UNDYING))
                return slot;

        }

        return -1;

    }

    private void consumeTotem(PlayerInventory inventory, int slot) {

        ItemStack totem = inventory.getItem(slot);
        if (totem == null)
            return;
        int remaining = totem.getAmount() - 1;
        if (remaining <= 0) {

            inventory.setItem(slot, null);

        } else {

            totem.setAmount(remaining);
            inventory.setItem(slot, totem);

        }

    }

    private void applyTotemEffects(Player player) {

        player.setFallDistance(0);
        player.setHealth(1);
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffects(this.totemEffects);

    }

}
