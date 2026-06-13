package net.redm1ne.hubthatred.utils;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class TeleportUtils
{
    private static HubThatRed plugin;
    public TeleportUtils(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }
    private static final Debugger debugger = new Debugger(TeleportUtils.class.getName());

    public static void teleportPlayer(double x, double y, double z, double yaw, double pitch, String worldName, String playerName)
    {
        Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z, (float)yaw, (float)pitch);
        Player player = plugin.getServer().getPlayer(playerName);
        if(player == null) return;

        fixInvisibilityBefore(location);
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                {
                    player.teleport(location);
                    fixInvisibilityAfter(player);
                }, 1);
    }

    public static void teleportPlayer(CommandSender sender, Player player, FileUtils.FileType type, String currentWorldName, boolean sendMessage)
    {
        if(player == null)
        {
            debugger.sendDebugMessage(Level.SEVERE, "Error: player who tried to teleport is NULL!");
            return;
        }

        String worldName;
        double x, y, z, yaw, pitch;

        if(type == FileUtils.FileType.HUB_YAML)
        {
            x = type.yaml.getDouble("hub.x");
            y = type.yaml.getDouble("hub.y");
            z = type.yaml.getDouble("hub.z");
            yaw = type.yaml.getDouble("hub.yaw");
            pitch = type.yaml.getDouble("hub.pitch");
            worldName = type.yaml.getString("hub.world");
        }
        else if(type == FileUtils.FileType.SPAWN_YAML)
        {
            x = type.yaml.getDouble("spawn.x." + currentWorldName);
            y = type.yaml.getDouble("spawn.y." + currentWorldName);
            z = type.yaml.getDouble("spawn.z." + currentWorldName);
            yaw = type.yaml.getDouble("spawn.yaw." + currentWorldName);
            pitch = type.yaml.getDouble("spawn.pitch." + currentWorldName);
            worldName = type.yaml.getString("spawn.world." + currentWorldName);
        }
        else
        {
            worldName = null;
            x = 0; y = 0; z = 0; yaw = 0; pitch = 0;
        }

        if(worldName == null)
        {
            debugger.sendDebugMessage(Level.SEVERE, "Error: could not find world!");
            if(type == FileUtils.FileType.HUB_YAML)
            {
                if(sendMessage) MessageUtils.sendLocalizedMessage(sender, LocalizedMessage.ERROR_HUB_NOT_SET);
            }
            else if(type == FileUtils.FileType.SPAWN_YAML)
            {
                if(sendMessage) MessageUtils.sendLocalizedMessage(sender, LocalizedMessage.ERROR_SPAWN_NOT_SET);
            }
            else
            {
                if(sendMessage) MessageUtils.sendColorizedMessage(sender, "&cError in code. Contact the developer!");
            }
            return;
        } else
        {
            debugger.sendDebugMessage(Level.INFO, "Found world name: " + worldName);

            if(worldName.equals("__UNSET__") && type == FileUtils.FileType.HUB_YAML)
            {
                if(sendMessage) MessageUtils.sendLocalizedMessage(sender, LocalizedMessage.ERROR_HUB_NOT_SET);
                return;
            }
        }

        World destinationWorld = plugin.getServer().getWorld(worldName);
        if(destinationWorld == null)
        {
            if(sendMessage)
            {
                String errorWorldNotExistingMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_WORLD_NOT_EXISTING, false);
                errorWorldNotExistingMessage = errorWorldNotExistingMessage.replace("%w%", worldName);
                MessageUtils.sendColorizedMessage(player, errorWorldNotExistingMessage);
            }
            return;
        }

        final Location finalLocation = new Location(destinationWorld, x, y, z, (float)yaw, (float)pitch);
        fixInvisibilityBefore(finalLocation);
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
        {
            player.teleport(finalLocation);
            fixInvisibilityAfter(player);

        }, 1);

        if(type == FileUtils.FileType.HUB_YAML)
        {
            if(sendMessage) MessageUtils.sendLocalizedMessage(player, LocalizedMessage.INFO_HUB_TELEPORTED);

            if((sender != player) && (sendMessage))
            {
                String message = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_HUB_TELEPORTED_OTHER, true).replace("%player%", player.getName());
                sender.sendMessage(message);
            }
        }
        else
        {
            if(sendMessage) MessageUtils.sendLocalizedMessage(player, LocalizedMessage.INFO_SPAWN_TELEPORTED);

            if((sender != player) && (sendMessage))
            {
                String message = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_SPAWN_TELEPORTED_OTHER, true).replace("%player%", player.getName()).replace("%world%", worldName);
                sender.sendMessage(message);
            }
        }
    }

    public static void teleportPlayer(CommandSender sender, Player player, FileUtils.FileType type, String currentWorldName)
    {
        teleportPlayer(sender, player, type, currentWorldName, true);
    }

    public static void teleportPlayer(CommandSender sender, Player player, FileUtils.FileType type, boolean sendMessage)
    {
        teleportPlayer(sender, player, type, null, sendMessage);
    }

    public static void teleportPlayer(CommandSender sender, Player player, FileUtils.FileType type)
    {
        teleportPlayer(sender, player, type, null);
    }

    public static void fixInvisibilityAfter(Player player)
    {
        if(PluginCache.invisibilityFix)
        {
            debugger.sendDebugMessage(Level.INFO, "Invisibility fix enabled!");
            player.getInventory().addItem(PluginCache.AIR);
        }
    }

    public static void fixInvisibilityBefore(Location destination)
    {
        destination.getChunk().load();
    }

}
