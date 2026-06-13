package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import net.redm1ne.hubthatred.utils.*;
import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.logging.Level;

public class SetSpawnCommand implements CommandExecutor
{

    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThatRed plugin;

    public SetSpawnCommand(HubThatRed givenPlugin) { plugin = givenPlugin; }


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


        if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_SET))
        {

            Player player = (Player)commandSender;
            Location playerLocation = player.getLocation();
            double x, y, z, yaw, pitch;
            String currentWorldName, worldYouAreSettingTheSpawnOf;
            x = playerLocation.getX();
            y = playerLocation.getY();
            z = playerLocation.getZ();
            yaw = playerLocation.getYaw();
            pitch = playerLocation.getPitch();
            currentWorldName = Objects.requireNonNull(playerLocation.getWorld()).getName();

            if(args.length > 0 && !args[0].equalsIgnoreCase(currentWorldName))
            {
                worldYouAreSettingTheSpawnOf = args[0];
            }
            else
            {
                worldYouAreSettingTheSpawnOf = currentWorldName;
                Objects.requireNonNull(plugin.getServer().getWorld(worldYouAreSettingTheSpawnOf)).setSpawnLocation((int)x, (int)y, (int)z);
            }

            World world = plugin.getServer().getWorld(worldYouAreSettingTheSpawnOf);
            if(world == null)
            {
                String errorWorldNotExistingMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.ERROR_WORLD_NOT_EXISTING, false);
                errorWorldNotExistingMessage = errorWorldNotExistingMessage.replace("%w%", worldYouAreSettingTheSpawnOf);
                MessageUtils.sendColorizedMessage(commandSender, errorWorldNotExistingMessage);
                return true;
            }

            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.world." + worldYouAreSettingTheSpawnOf, currentWorldName);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.x." + worldYouAreSettingTheSpawnOf, x);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.y." + worldYouAreSettingTheSpawnOf, y);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.z." + worldYouAreSettingTheSpawnOf, z);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.yaw." + worldYouAreSettingTheSpawnOf, yaw);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.pitch." + worldYouAreSettingTheSpawnOf, pitch);

            FileUtils.saveExistingYaml(FileUtils.FileType.SPAWN_YAML);

            String spawnSetMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_SPAWN_SET, false)
            .replace("%dw%", worldYouAreSettingTheSpawnOf)
            .replace("%cw%", currentWorldName)
            .replace("%x%", (int)x + "")
            .replace("%y%", (int)y + "")
            .replace("%z%", (int)z + "");

            MessageUtils.sendColorizedMessage(commandSender, spawnSetMessage);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_SET.permission);
            commandSender.sendMessage(errorMessage);
        }




        return true;
    }
}
