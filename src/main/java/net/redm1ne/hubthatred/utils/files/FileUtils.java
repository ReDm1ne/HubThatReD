package net.redm1ne.hubthatred.utils.files;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.PluginCache;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.Debugger;
import net.redm1ne.hubthatred.utils.metrics.UpdateChecker;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class FileUtils
{

    private static final Debugger debugger = new Debugger(FileUtils.class.getName());

    private static HubThatRed plugin;

    public FileUtils(HubThatRed plugin) {
        FileUtils.plugin = plugin;
    }

    public static void copyFileFromSrc(FileType givenFileType)
    {
        if(!givenFileType.file.exists())
        {
            InputStream is = FileUtils.class.getResourceAsStream("/" + givenFileType.file.getName());
            try
            {
                Files.copy(is, Paths.get(givenFileType.file.getAbsolutePath()));
                is.close();
                debugger.sendDebugMessage(Level.INFO, "File " + givenFileType.file.getName() + " successfully created.");
            }
            catch (IOException e)
            {
                HubThatRed.logger.log(Level.SEVERE, "There were some unexpected errors from " + givenFileType.file.getName() + " file creation. Please contact the developer and send him this log:");
                e.printStackTrace();
            }

        }
    }

    public static void reloadYamls()
    {
        for(FileType fileType : FileType.values())
        {
            fileType.yaml = YamlConfiguration.loadConfiguration(fileType.file);
        }

        YamlConfiguration config = FileType.CONFIG_YAML.yaml;

        if(config.getBoolean(ConfigEntry.UPDATE_CHECKER_ENABLED.path))
        {
            PluginCache.updateChecker = true;
            if(UpdateChecker.task != null)
            {
                plugin.getServer().getScheduler().cancelTask(UpdateChecker.task.getTaskId());
            }

            UpdateChecker.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, plugin.updateChecker, 1, 20 * 60 * 60 * 12);
        }
        else
        {
            PluginCache.updateChecker = false;
        }

        PluginCache.invisibilityFix = config.getBoolean(ConfigEntry.INVISIBILITY_FIX.path);
        PluginCache.sendJoinTpMessage = config.getBoolean(ConfigEntry.TELEPORTATION_TP_MESSAGE_ON_JOIN.path);
    }

    public static void reloadYaml(FileType yamlFile)
    {
        yamlFile.yaml = YamlConfiguration.loadConfiguration(yamlFile.file);
        debugger.sendDebugMessage(Level.INFO, "File " + yamlFile.file.getName() + " YAML loaded.");
    }

    public static void saveExistingYaml(FileType yamlFile)
    {
        File configFile = yamlFile.file;
        try {
            yamlFile.yaml.save(configFile);
            debugger.sendDebugMessage(Level.INFO, "Successfully saved " + configFile.getName() +" (YAML)!");
        } catch (IOException e) {
            debugger.sendDebugMessage(Level.SEVERE, "Error in saving " + configFile.getName() + "(YAML)!");
            e.printStackTrace();
        }

        reloadYaml(yamlFile);
    }


    public static void checkFiles() {
        if(!plugin.getDataFolder().exists())
        {
            if(plugin.getDataFolder().mkdir())
            {
                debugger.sendDebugMessage(Level.INFO, "Plugin dir successfully created.");
            }
        }

        for(FileType file : FileType.values())
        {

            copyFileFromSrc(file);
            reloadYaml(file);
            checkYamlMissingEntries(file);
        }


        HubThatRed.logger.log(Level.INFO, "All files are working correctly.");
    }


    private static void checkYamlMissingEntries(FileType givenFile)
    {
        InputStream is = FileUtils.class.getResourceAsStream("/" + givenFile.file.getName());

        Reader targetReader = new InputStreamReader(is);

        YamlConfiguration srcYaml = YamlConfiguration.loadConfiguration(targetReader);

        debugger.sendDebugMessage(Level.INFO, "Iterating src config entries for file " + givenFile.file.getName() + ".");

        for (String key : srcYaml.getConfigurationSection("").getKeys(true))
        {
            debugger.sendDebugMessage(Level.INFO, "Analyzing key '" + key + "' with default value '" + srcYaml.get(key) + "'.");

            if(!givenFile.yaml.contains(key))
            {
                debugger.sendDebugMessage(Level.WARNING, "Config file is missing '" + key + "' key! Proceeding to add it...");
                givenFile.yaml.set(key, srcYaml.get(key));
                debugger.sendDebugMessage(Level.WARNING, "Added key '" + key + "' with value '" + srcYaml.get(key) + "'.");
                saveExistingYaml(givenFile);
            }
        }
        debugger.sendDebugMessage(Level.INFO, " Done iterating src config entries for file " + givenFile.file.getName() + "!");
    }

    public enum FileType
    {
        CONFIG_YAML(new File(plugin.getDataFolder()+File.separator + "config.yml"), new YamlConfiguration()),
        LANG_YAML(new File(plugin.getDataFolder()+File.separator + "lang.yml"), new YamlConfiguration()),
        SPAWN_YAML(new File(plugin.getDataFolder()+File.separator + "spawn.yml"), new YamlConfiguration()),
        HUB_YAML(new File(plugin.getDataFolder()+File.separator + "hub.yml"), new YamlConfiguration());

        public File file;
        public YamlConfiguration yaml;
        FileType(File givenFile, YamlConfiguration yamlConfig)
        {
            file = givenFile;
            yaml = yamlConfig;
        }
    }
}
