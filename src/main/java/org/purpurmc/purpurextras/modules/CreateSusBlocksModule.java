package org.purpurmc.purpurextras.modules;

import org.bukkit.event.Listener;
import org.purpurmc.purpurextras.PurpurExtrasOG;

/**
 * If enabled, players will be able to shift-right click on sand and gravel with items in their hands to create
 * suspicious blocks and put held item inside. Held item will disappear from player's hand and will be added as loot
 * inside the suspicious block. Only one item can be added per block.
 */
public class CreateSusBlocksModule implements PurpurExtrasModule, Listener {

    protected CreateSusBlocksModule() {}

    @Override
    public void enable() {
        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        // return PurpurExtrasOG.getPurpurConfig().getBoolean("settings.create-suspicious-blocks", false);
        return false;
    }

    /*@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractWithSusBlock(PlayerInteractEvent event) {
    	if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    	Player player = event.getPlayer();
    	if (!player.isSneaking()) return;
    	Block block = event.getClickedBlock();
    	if (block == null) return;
    	if (block.getType() != Material.SAND && block.getType() != Material.GRAVEL) return;
    	ItemStack itemStack = event.getItem();
    	if (itemStack == null) return;

    	switch (block.getType()) {
    	case SAND -> block.setType(Material.SUSPICIOUS_SAND);
    	case GRAVEL -> block.setType(Material.SUSPICIOUS_GRAVEL);
    	default -> {
    		return;
    	}
    	}

    	BlockState blockState = block.getState();
    	if (!(blockState instanceof BrushableBlock brushableBlock)) return;
    	brushableBlock.setItem(itemStack.asOne());
    	event.getItem().setAmount(itemStack.getAmount() - 1);
    	brushableBlock.update();
    	event.setUseInteractedBlock(Event.Result.DENY);
    	event.setUseItemInHand(Event.Result.DENY);

    	EquipmentSlot hand = event.getHand();
    	if (hand == null) return;
    	player.swingHand(hand);
    }*/
}
