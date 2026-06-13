package net.redm1ne.hubthatred;

import net.redm1ne.hubthatred.commands.*;
import net.redm1ne.hubthatred.completers.InfoCompleter;
import net.redm1ne.hubthatred.completers.SpawnCompleter;
import net.redm1ne.hubthatred.listeners.PlayerChatListener;
import net.redm1ne.hubthatred.listeners.PlayerJoinListener;
import net.redm1ne.hubthatred.listeners.PlayerMoveListener;
import net.redm1ne.hubthatred.listeners.PlayerRespawnListener;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.Debugger;
import net.redm1ne.hubthatred.utils.TeleportUtils;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import net.redm1ne.hubthatred.utils.files.OldConfigConversion;
import net.redm1ne.hubthatred.utils.metrics.Metrics;
import net.redm1ne.hubthatred.utils.metrics.UpdateChecker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HubThatRed extends JavaPlugin
{
    private final Debugger debugger = new Debugger(getClass().getName());

    public static Logger logger;
    private PluginManager pluginManager;
    public UpdateChecker updateChecker;
    private static HubThatRed instance;

    public HubThatRed()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        logger = getLogger();
        pluginManager = getServer().getPluginManager();

        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZING PLUGIN ]---");
        debugger.sendDebugMessage(Level.INFO, "Logger and PluginManager initialized.");

        debugger.sendDebugMessage(Level.INFO, "Loading classes...");

        FileUtils fileUtilsInstance = new FileUtils(this);

        HubThatCommand hubThatCommandInstance = new HubThatCommand(this);
        HubCommand hubCommandInstance = new HubCommand(this);
        SpawnCommand spawnCommandInstance = new SpawnCommand(this);
        SetSpawnCommand setSpawnCommandInstance = new SetSpawnCommand(this);
        WorldListCommand worldListCommandInstance = new WorldListCommand(this);
        WorldTpCommand worldTpCommandInstance = new WorldTpCommand(this);

        TeleportUtils teleportUtilsInstance = new TeleportUtils(this);
        updateChecker = new UpdateChecker(this);
        debugger.sendDebugMessage(Level.INFO, "Classes loaded!");

        debugger.sendDebugMessage(Level.INFO, "Registering listeners...");
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new PlayerRespawnListener(this), this);

        debugger.sendDebugMessage(Level.INFO, "Listeners registered!");

        debugger.sendDebugMessage(Level.INFO, "Registering commands...");

        getCommand("hubthat").setExecutor(hubThatCommandInstance);
        getCommand("hubthat").setTabCompleter(new InfoCompleter());

        getCommand("hub").setExecutor(hubCommandInstance);

        getCommand("sethub").setExecutor(new SetHubCommand());

        getCommand("spawn").setExecutor(spawnCommandInstance);
        getCommand("spawn").setTabCompleter(new SpawnCompleter());

        getCommand("setspawn").setExecutor(setSpawnCommandInstance);
        getCommand("setspawn").setTabCompleter(new SpawnCompleter());

        getCommand("worldlist").setExecutor(worldListCommandInstance);

        getCommand("worldtp").setExecutor(worldTpCommandInstance);
        getCommand("worldtp").setTabCompleter(new SpawnCompleter());

        debugger.sendDebugMessage(Level.INFO, "Commands registered!");

        debugger.sendDebugMessage(Level.INFO, "Checking files...");
        OldConfigConversion.checkOldConfig(this, logger);
        FileUtils.checkFiles();
        debugger.sendDebugMessage(Level.INFO, "Done checking files!");

        debugger.sendDebugMessage(Level.INFO, "Loading configuration...");
        FileUtils.reloadYamls();
        debugger.sendDebugMessage(Level.INFO, "Configuration loaded!");

        debugger.sendDebugMessage(Level.INFO, "Setting up Metrics...");
        setupMetrics();
        debugger.sendDebugMessage(Level.INFO, "Done setting up Metrics!");

        logger.log(Level.INFO, getDescription().getName() + " successfully loaded!");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZATION DONE ]---");
    }

    @Override
    public void onDisable()
    {
        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ DISABLING PLUGIN ]---");
        getServer().getScheduler().cancelTasks(this);
        logger.log(Level.INFO, getDescription().getName() + " unloaded!");
        debugger.sendDebugMessage(Level.WARNING, "---[ PLUGIN DISABLED ]---");
    }

    private void setupMetrics()
    {
        Metrics metrics = new Metrics(this);

        YamlConfiguration config = FileUtils.FileType.CONFIG_YAML.yaml;
        metrics.addCustomChart(new Metrics.SimplePie("respawn-handler", () -> config.getString(ConfigEntry.TELEPORTATION_RESPAWN_HANDLER.path)));
        metrics.addCustomChart(new Metrics.SimplePie("world-related-chat", () -> config.getString(ConfigEntry.WORLD_RELATED_CHAT.path)));
        metrics.addCustomChart(new Metrics.SimplePie("update-notify", () -> config.getString(ConfigEntry.UPDATE_CHECKER_ENABLED.path)));
        metrics.addCustomChart(new Metrics.SimplePie("set-gamemode-on-join", () -> config.getString(ConfigEntry.GAMEMODE_SET_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("tp-hub-on-join", () -> config.getString(ConfigEntry.TELEPORTATION_TP_HUB_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("tp-hub-on-respawn", () -> config.getString(ConfigEntry.TELEPORTATION_TP_HUB_ON_RESPAWN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("multiverse-bypass", () -> config.getString(ConfigEntry.MULTIVERSE_BYPASS.path)));
        metrics.addCustomChart(new Metrics.SimplePie("send-tp-message-on-join", () -> config.getString(ConfigEntry.TELEPORTATION_TP_MESSAGE_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("fix-invisible-after-tp", () -> config.getString(ConfigEntry.INVISIBILITY_FIX.path)));

        if (config.getBoolean(ConfigEntry.GAMEMODE_SET_ON_JOIN.path))
        {
            metrics.addCustomChart(new Metrics.SimplePie("join-gamemode", () -> config.getString(ConfigEntry.GAMEMODE.path)));
        }
    }

    public static HubThatRed getInstance()
    {
        return instance;
    }
}
