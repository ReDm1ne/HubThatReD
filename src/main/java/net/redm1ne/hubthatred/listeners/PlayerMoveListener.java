package net.redm1ne.hubthatred.listeners;

import net.redm1ne.hubthatred.utils.PluginCache;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.LocalizedMessage;
import net.redm1ne.hubthatred.utils.MessageUtils;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener
{

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {

        if(FileUtils.FileType.CONFIG_YAML.yaml.getBoolean(ConfigEntry.MOVEMENT_DETECTION_ENABLED.path))
        {
            String playerName = event.getPlayer().getName();

            if (PluginCache.teleporting.contains(playerName))
            {
                if(event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ())
                {
                    PluginCache.teleporting.remove(playerName);
                    PluginCache.cancelRunnable.add(playerName);
                    MessageUtils.sendLocalizedMessage(event.getPlayer(), LocalizedMessage.WARNING_TELEPORTATION_CANCELLED);
                }
            }
        }
    }
}
