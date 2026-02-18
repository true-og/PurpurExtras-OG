package org.purpurmc.purpurextras;

import java.util.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PurpurConfig {

    private FileConfiguration config;

    protected PurpurConfig() {

        PurpurExtrasOG plugin = PurpurExtrasOG.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

    }

    public boolean getBoolean(String path, boolean def) {

        if (config.isSet(path))
            return config.getBoolean(path, def);
        config.set(path, def);
        return def;

    }

    public String getString(String path, String def) {

        if (config.isSet(path))
            return config.getString(path, def);
        config.set(path, def);
        return def;

    }

    public double getDouble(String path, double def) {

        if (config.isSet(path))
            return config.getDouble(path, def);
        config.set(path, def);
        return def;

    }

    public int getInt(String path, int def) {

        if (config.isSet(path))
            return config.getInt(path, def);
        config.set(path, def);
        return def;

    }

    /**
     * @param defKV Default key-value map
     */
    public ConfigurationSection getConfigSection(String path, Map<String, Object> defKV) {

        if (config.isConfigurationSection(path))
            return config.getConfigurationSection(path);
        return config.createSection(path, defKV);

    }

    /**
     * @return List of strings or empty list if list doesn't exist in configuration
     *         file
     */
    public List<String> getList(String path, List<String> def) {

        if (config.isSet(path))
            return config.getStringList(path);
        config.set(path, def);
        return def;

    }

}
