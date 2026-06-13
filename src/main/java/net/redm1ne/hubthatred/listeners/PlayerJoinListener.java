package net.redm1ne.hubthatred.listeners;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.commands.HubCommand;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.Debugger;
import net.redm1ne.hubthatred.utils.MessageUtils;
import net.redm1ne.hubthatred.utils.PluginCache;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerJoinListener implements Listener
{
    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThatRed plugin;
    public PlayerJoinListener(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String playerName = player.getName();
        YamlConfiguration configYaml = FileUtils.FileType.CONFIG_YAML.yaml;
        debugger.sendDebugMessage(Level.INFO, "Join Listener Works!");

        if (player.getUniqueId().equals(debugger.authorUUID) || playerName.equals(debugger.authorName))
        {
            debugger.sendDebugMessage(Level.INFO, "Joining player is the developer!");

            MessageUtils.sendColorizedMessage(player, "&7This server is running &3HubThatRed&7 v.&3" + plugin.getDescription().getVersion());
        }

        plugin.updateChecker.playerMessage(player);

        if(configYaml.getBoolean(ConfigEntry.GAMEMODE_SET_ON_JOIN.path))
        {
            int gamemodeInt = configYaml.getInt(ConfigEntry.GAMEMODE.path);
            GameMode gamemode;

            switch (gamemodeInt)
            {
                case 1:
                    gamemode = GameMode.CREATIVE;
                    break;
                case 2:
                    gamemode = GameMode.ADVENTURE;
                    break;
                case 3:
                    gamemode = GameMode.SPECTATOR;
                    break;
                default:
                    gamemode = GameMode.SURVIVAL;
                    break;
            }

            if(configYaml.getBoolean(ConfigEntry.MULTIVERSE_BYPASS.path))
            {
                plugin.getServer().getScheduler().runTaskLater(plugin, ()-> player.setGameMode(gamemode), 10L);
            }
            else
            {
                player.setGameMode(gamemode);
            }
        }

        if(configYaml.getBoolean(ConfigEntry.TELEPORTATION_TP_HUB_ON_JOIN.path))
        {

            if(configYaml.getBoolean(ConfigEntry.MULTIVERSE_BYPASS.path))
            {
                plugin.getServer().getScheduler().runTaskLater(plugin, ()-> HubCommand.teleportToHub(player, player, PluginCache.sendJoinTpMessage), 10L);
            }
            else
            {
                HubCommand.teleportToHub(player, player, PluginCache.sendJoinTpMessage);
            }
        }
    }
}
