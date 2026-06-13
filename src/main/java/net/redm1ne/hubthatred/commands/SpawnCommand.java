package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.*;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;


public class SpawnCommand  implements CommandExecutor
{

    private static final Debugger debugger = new Debugger(SpawnCommand.class.getName());

    private final HubThatRed plugin;

    public SpawnCommand(HubThatRed givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {


        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        String username = commandSender.getName();

        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }

        if(args.length > 1)
        {
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT_OTHERS))
            {
                String teleportingPlayerName = args[1];
                String worldName = args[0];

                Player teleportPlayer = plugin.getServer().getPlayer(teleportingPlayerName);
                if(teleportPlayer == null)
                {
                    String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_PLAYER_OFFLINE, true).replace("%player%", teleportingPlayerName);
                    commandSender.sendMessage(errorMessage);
                    return true;
                }

                if(plugin.getServer().getWorld(worldName) == null)
                {
                    MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_SPAWN_NOT_SET);
                    return true;
                }

                teleportToSpawn(commandSender, teleportPlayer, worldName);
                return true;
            }
            else
            {
                String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT_OTHERS.permission);
                commandSender.sendMessage(errorMessage);
                return true;
            }
        }

        if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT))
        {


            if(PermissionUtils.playerHasPermission(commandSender, Permissions.NO_SPAWN_DELAY))
            {

                checkArgsAndTeleport(args, commandSender);

                return true;
            }
            else
            {
                if(!PluginCache.teleporting.contains(username))
                {
                    if(args.length > 0)
                    {
                        if(!PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT_ANOTHER_WORLD))
                        {
                            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT_ANOTHER_WORLD.permission);
                            commandSender.sendMessage(errorMessage);
                            return true;
                        }

                        if(plugin.getServer().getWorld(args[0]) == null)
                        {
                            String erroMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_WORLD_NOT_EXISTING, true).replace("%w%", args[0]);
                            commandSender.sendMessage(erroMessage);
                            return true;
                        }
                    }


                    int delay = FileUtils.FileType.CONFIG_YAML.yaml.getInt(ConfigEntry.SPAWN_DELAY.path);

                    String delayMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_TELEPORT_DELAY, false);
                    delayMessage = delayMessage.replace("%delay%", delay + "");
                    MessageUtils.sendColorizedMessage(commandSender, delayMessage);

                    PluginCache.teleporting.add(username);
                    Bukkit.getScheduler().runTaskLater(plugin,() ->
                    {
                            if(!PluginCache.cancelRunnable.contains(username) && PluginCache.teleporting.contains(username))
                            {

                                checkArgsAndTeleport(args, commandSender);

                            }
                            PluginCache.cancelRunnable.remove(username);

                    }, delay * 20L);
                    return true;
                }
                else
                {
                    MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_ALREADY_TELEPORTING);
                    return true;
                }
            }
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT.permission);
            commandSender.sendMessage(errorMessage);
        }

        return true;
    }


    private void checkArgsAndTeleport(String[] args, CommandSender commandSender)
    {
        String worldName;

        Player player = (Player)commandSender;

        String username = commandSender.getName();

        if(args.length > 0)
        {

                worldName = args[0];
        }
        else
        {
            worldName = player.getWorld().getName();
        }

        teleportToSpawn(player, player, worldName);
    }


    public static void teleportToSpawn(CommandSender sender, Player player, String worldName)
    {
        String username = player.getName();

        debugger.sendDebugMessage(Level.INFO, "Player name: " + username + "; World name: " + worldName);
        TeleportUtils.teleportPlayer(sender, player, FileUtils.FileType.SPAWN_YAML, worldName);
        PluginCache.teleporting.remove(username);
    }
}
