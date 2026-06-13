package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.*;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class HubCommand  implements CommandExecutor
{
    private static final Debugger debugger = new Debugger(HubCommand.class.getName());

    private final HubThatRed plugin;

    public HubCommand(HubThatRed givenPlugin) { plugin = givenPlugin; }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {

        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        String username = commandSender.getName();

        if(args.length > 0)
        {
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_TELEPORT_OTHERS))
            {
                String teleportingPlayerName = args[0];
                Player teleportingPlayer = plugin.getServer().getPlayer(teleportingPlayerName);
                if(teleportingPlayer == null)
                {
                    String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_PLAYER_OFFLINE, true).replace("%player%", teleportingPlayerName);
                    commandSender.sendMessage(errorMessage);
                    return true;
                }
                else
                {
                    teleportToHub(commandSender, teleportingPlayer);
                    return true;
                }
            }
            else
            {
                String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_TELEPORT_OTHERS.permission);
                commandSender.sendMessage(errorMessage);
                return true;
            }
        }


        boolean senderIsConsole = !(commandSender instanceof Player);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }


        if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_TELEPORT))
        {

            if(PermissionUtils.playerHasPermission(commandSender, Permissions.NO_HUB_DELAY))
            {

                teleportToHub(commandSender, (Player)commandSender);
                return true;
            }
            else
            {
                if(!PluginCache.teleporting.contains(username))
                {
                    PluginCache.teleporting.add(username);
                    int delay = FileUtils.FileType.CONFIG_YAML.yaml.getInt(ConfigEntry.HUB_DELAY.path);


                    String delayMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_TELEPORT_DELAY, false);
                    delayMessage = delayMessage.replace("%delay%", delay + "");
                    MessageUtils.sendColorizedMessage(commandSender, delayMessage);

                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(!PluginCache.cancelRunnable.contains(username) && PluginCache.teleporting.contains(username))
                            {
                                teleportToHub(commandSender, (Player)commandSender);
                            }
                            PluginCache.cancelRunnable.remove(username);

                        }
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
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_TELEPORT.permission);
            commandSender.sendMessage(errorMessage);
            return true;
        }
    }

    public static void teleportToHub(CommandSender actor, Player player, boolean sendMessage)
    {
        String username = player.getName();

        TeleportUtils.teleportPlayer(actor, player, FileUtils.FileType.HUB_YAML, sendMessage);

        PluginCache.teleporting.remove(username);
    }

    public static void teleportToHub(CommandSender actor, Player player)
    {
        teleportToHub(actor, player, true);
    }
}
