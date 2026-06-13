package net.redm1ne.hubthatred.commands.hubthatcommands;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.*;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ReloadCommand
{
    private static final Debugger debugger = new Debugger(ReloadCommand.class.getName());


    public static void reloadCommand(CommandSender commandSender, HubThatRed plugin)
    {
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.RELOAD_CONFIG))
        {
            debugger.sendDebugMessage(Level.INFO, "Reloading YAMLS...");
            MessageUtils.sendColorizedMessage(commandSender, "&7Reloading &e" + plugin.getName() + "&7 v&e" + plugin.getDescription().getVersion() + "&7...");
            FileUtils.checkFiles();
            FileUtils.reloadYamls();
            MessageUtils.sendColorizedMessage(commandSender, "&eReloaded!");
            debugger.sendDebugMessage(Level.INFO, "Reloaded YAMLs!");

            plugin.updateChecker.playerMessage(commandSender);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.RELOAD_CONFIG.permission);
            commandSender.sendMessage(errorMessage);
        }


    }
}
