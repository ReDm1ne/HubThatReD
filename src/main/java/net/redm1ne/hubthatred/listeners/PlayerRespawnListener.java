package net.redm1ne.hubthatred.listeners;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.commands.HubCommand;
import net.redm1ne.hubthatred.commands.SpawnCommand;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerRespawnListener implements Listener
{

    private final HubThatRed plugin;
    public PlayerRespawnListener(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }



    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        YamlConfiguration configYaml = FileUtils.FileType.CONFIG_YAML.yaml;



        if(configYaml.getBoolean(ConfigEntry.MULTIVERSE_BYPASS.path))
        {
            plugin.getServer().getScheduler().runTaskLater(plugin, ()->  tpPlayer(event.getPlayer(), configYaml), 5L);

        }
        else
        {

            tpPlayer(event.getPlayer(), configYaml);
        }


    }

    private void tpPlayer(Player player, YamlConfiguration configYaml)
    {
        if(configYaml.getBoolean(ConfigEntry.TELEPORTATION_RESPAWN_HANDLER.path))
        {

            if(configYaml.getBoolean(ConfigEntry.TELEPORTATION_TP_HUB_ON_RESPAWN.path))
            {
                HubCommand.teleportToHub(player, player);
            }
            else
            {
                SpawnCommand.teleportToSpawn(player, player, player.getWorld().getName());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        // Empty implementation - original had commented out NMS code
    }
}
