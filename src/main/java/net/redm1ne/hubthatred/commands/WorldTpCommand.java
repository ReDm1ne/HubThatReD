package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import net.redm1ne.hubthatred.utils.*;

import java.util.logging.Level;

import static net.redm1ne.hubthatred.utils.TeleportUtils.fixInvisibilityAfter;
import static net.redm1ne.hubthatred.utils.TeleportUtils.fixInvisibilityBefore;

public class WorldTpCommand implements CommandExecutor
{

    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThatRed plugin;

    public WorldTpCommand(HubThatRed givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());


        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }

        if(PermissionUtils.playerHasPermission(commandSender, Permissions.TELEPORT_TO_WORLD))
        {
            if(args.length != 1)
            {
                String wrongUsageMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_WRONG_USAGE, false).replace("%usage%", "/worldtp <world>");
                MessageUtils.sendColorizedMessage(commandSender, wrongUsageMessage);
                return true;
            }
            String destinationWorldName = args[0];
            World destinationWorld = plugin.getServer().getWorld(destinationWorldName);

            if(destinationWorld == null)
            {
                String worldDoesNotExistMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_WORLD_NOT_EXISTING, false);
                worldDoesNotExistMessage = worldDoesNotExistMessage.replace("%w%", destinationWorldName);
                MessageUtils.sendColorizedMessage(commandSender, worldDoesNotExistMessage);
                return true;
            }

            Location destinationLocation = new Location(destinationWorld, destinationWorld.getSpawnLocation().getX(), destinationWorld.getSpawnLocation().getY(), destinationWorld.getSpawnLocation().getZ(), destinationWorld.getSpawnLocation().getYaw(), destinationWorld.getSpawnLocation().getPitch());
            Player player = (Player)commandSender;
            fixInvisibilityBefore(destinationLocation);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.teleport(destinationLocation), 1);
            fixInvisibilityAfter(player);
            String teleportedMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_WORLDTP_TELEPORTED, false);
            teleportedMessage = teleportedMessage.replace("%world%", destinationWorldName).replace("%w%", destinationWorldName);
            MessageUtils.sendColorizedMessage(commandSender, teleportedMessage);
        }

        return true;
    }
}
