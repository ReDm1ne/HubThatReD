package net.redm1ne.hubthatred.listeners;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.ConfigEntry;
import net.redm1ne.hubthatred.utils.Debugger;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener
{

    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThatRed plugin;
    public PlayerChatListener(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player messageSender = event.getPlayer();

        if(FileUtils.FileType.CONFIG_YAML.yaml.getBoolean(ConfigEntry.WORLD_RELATED_CHAT.path))
        {

            String senderWorldSpawn = FileUtils.FileType.SPAWN_YAML.yaml.getString("spawn.world." + messageSender.getWorld().getName(), "__UNSET__");

            for(Player messageReceiver : plugin.getServer().getOnlinePlayers())
            {
                String receiverWorldSpawn = FileUtils.FileType.SPAWN_YAML.yaml.getString("spawn.world." + messageReceiver.getWorld().getName(), "__UNSET__");

                if(!senderWorldSpawn.equalsIgnoreCase(receiverWorldSpawn))
                {
                    event.getRecipients().remove(messageReceiver);
                }
            }
        }
    }
}
