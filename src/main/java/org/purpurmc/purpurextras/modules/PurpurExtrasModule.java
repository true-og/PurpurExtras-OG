package org.purpurmc.purpurextras.modules;

import java.util.*;
import org.bukkit.event.HandlerList;
import org.purpurmc.purpurextras.PurpurExtrasOG;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public interface PurpurExtrasModule {

    Reflections reflections = new Reflections("org.purpurmc.purpurextras.modules");

    /**
     * Enables the feature, registers the listeners.
     */
    void enable();

    /**
     * @return true if the feature should be enabled
     */
    boolean shouldEnable();

    static void reloadModules() {

        HandlerList.unregisterAll(PurpurExtrasOG.getInstance());

        Set<Class<?>> subTypes = reflections.get(Scanners.SubTypes.of(PurpurExtrasModule.class).asClass());

        subTypes.forEach(clazz -> {

            try {

                PurpurExtrasModule module = (PurpurExtrasModule) clazz.getDeclaredConstructor().newInstance();
                if (module.shouldEnable()) {

                    module.enable();

                }

            } catch (Exception e) {

                PurpurExtrasOG.getInstance().getSLF4JLogger().warn("Failed to load module " + clazz.getSimpleName());

            }

        });

    }

}
