package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.utils.*;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SetHubCommand implements CommandExecutor
{

    private final Debugger debugger = new Debugger(getClass().getName());

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

        if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_SET))
        {
            Player player = (Player)commandSender;
            Location playerLocation = player.getLocation();
            double x, y, z, yaw, pitch;
            String worldName;
            x = playerLocation.getX();
            y = playerLocation.getY();
            z = playerLocation.getZ();
            yaw = playerLocation.getYaw();
            pitch = playerLocation.getPitch();
            worldName = playerLocation.getWorld().getName();

            FileUtils.FileType.HUB_YAML.yaml.set("hub.x", x);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.y", y);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.z", z);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.yaw", yaw);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.pitch", pitch);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.world", worldName);

            FileUtils.saveExistingYaml(FileUtils.FileType.HUB_YAML);

            String hubSetMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_HUB_SET, false);
            hubSetMessage = hubSetMessage.replace("%w%", worldName);
            hubSetMessage = hubSetMessage.replace("%x%", (int)x + "");
            hubSetMessage = hubSetMessage.replace("%y%", (int)y + "");
            hubSetMessage = hubSetMessage.replace("%z%", (int)z + "");
            MessageUtils.sendColorizedMessage(commandSender, hubSetMessage);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_SET.permission);
            commandSender.sendMessage(errorMessage);
        }
        return true;
    }
}
