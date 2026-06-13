package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.commands.hubthatcommands.HelpCommand;
import net.redm1ne.hubthatred.commands.hubthatcommands.ReloadCommand;
import net.redm1ne.hubthatred.utils.Debugger;
import net.redm1ne.hubthatred.utils.MessageUtils;
import net.redm1ne.hubthatred.utils.PluginCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class HubThatCommand implements CommandExecutor
{

    private final HubThatRed plugin;

    private final Debugger debugger = new Debugger(getClass().getName());

    public HubThatCommand(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        if(args.length == 0)
        {
            MessageUtils.sendColorizedMessage(commandSender, "&6" + plugin.getName() +"&7 version &6" + plugin.getDescription().getVersion() + "&7 for &6SpigotMC/CraftBukkit &6" + PluginCache.minSupportedVersion + "&7-&6" + PluginCache.maxSupportedVersion + "&7.");
            MessageUtils.sendColorizedMessage(commandSender, "&7Coded by &6" + debugger.authorName + "&7, &6GNU GPLv3&7.");
            commandSender.sendMessage("");
            MessageUtils.sendColorizedMessage(commandSender, "&7Write &6/"+ plugin.getName().toLowerCase() + " help&7 to see plugin commands.");
        }
        else if (args.length == 1)
        {
            if(args[0].equalsIgnoreCase("help"))
            {
                HelpCommand.infoCommand(commandSender, plugin);
            }
            else if(args[0].equalsIgnoreCase("reload"))
            {
                ReloadCommand.reloadCommand(commandSender, plugin);
            }
        }
        return true;
    }
}
