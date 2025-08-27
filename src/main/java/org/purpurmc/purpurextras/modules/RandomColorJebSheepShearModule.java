package org.purpurmc.purpurextras.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtrasOG;

/**
 * If enabled causes sheep named jeb_ to drop random colors of wool
 */
public class RandomColorJebSheepShearModule implements PurpurExtrasModule, Listener {

    private final List<Material> coloredWool = new ArrayList<>();

    protected RandomColorJebSheepShearModule() {

        this.coloredWool.add(Material.BLACK_WOOL);
        this.coloredWool.add(Material.BLUE_WOOL);
        this.coloredWool.add(Material.BROWN_WOOL);
        this.coloredWool.add(Material.CYAN_WOOL);
        this.coloredWool.add(Material.GRAY_WOOL);
        this.coloredWool.add(Material.GREEN_WOOL);
        this.coloredWool.add(Material.LIGHT_BLUE_WOOL);
        this.coloredWool.add(Material.LIGHT_GRAY_WOOL);
        this.coloredWool.add(Material.LIME_WOOL);
        this.coloredWool.add(Material.MAGENTA_WOOL);
        this.coloredWool.add(Material.ORANGE_WOOL);
        this.coloredWool.add(Material.PINK_WOOL);
        this.coloredWool.add(Material.PURPLE_WOOL);
        this.coloredWool.add(Material.RED_WOOL);
        this.coloredWool.add(Material.WHITE_WOOL);
        this.coloredWool.add(Material.YELLOW_WOOL);

    }

    @Override
    public void enable() {

        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public boolean shouldEnable() {

        return PurpurExtrasOG.getPurpurConfig().getBoolean("settings.mobs.sheep.jeb-shear-random-color", false);

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJebSheepShear(PlayerShearEntityEvent event) {

        if (!(event.getEntity() instanceof Sheep sheep))
            return;
        if (!isJebSheep(sheep))
            return;

        // Remove default drops
        event.setCancelled(true);

        // Add custom drops
        int randomIndex = new Random().nextInt(coloredWool.size());
        ItemStack customWool = new ItemStack(coloredWool.get(randomIndex));
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), customWool);

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJebSheepShear(BlockShearEntityEvent event) {

        if (!(event.getEntity() instanceof Sheep sheep))
            return;
        if (!isJebSheep(sheep))
            return;

        // Remove default drops
        event.setCancelled(true);

        // Add custom drops
        int randomIndex = new Random().nextInt(coloredWool.size());
        ItemStack customWool = new ItemStack(coloredWool.get(randomIndex));
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), customWool);

    }

    // Helper method to check if the sheep is a Jeb Sheep.
    private boolean isJebSheep(Sheep sheep) {

        Component customName = sheep.customName();
        if (customName != null) {

            String name = PlainTextComponentSerializer.plainText().serialize(customName);
            return "jeb_".equals(name);

        }

        return false;

    }

}
