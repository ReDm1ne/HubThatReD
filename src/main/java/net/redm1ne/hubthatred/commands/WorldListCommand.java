package net.redm1ne.hubthatred.commands;

import net.redm1ne.hubthatred.HubThatRed;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.redm1ne.hubthatred.utils.*;

import java.util.logging.Level;

public class WorldListCommand implements CommandExecutor
{


    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThatRed plugin;

    public WorldListCommand(HubThatRed givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());


        if(PermissionUtils.playerHasPermission(commandSender, Permissions.WORLD_LIST))
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.INFO_WORLDS_LIST);
            MessageUtils.sendColorizedMessage(commandSender, "&7---------");


            int i = 0;
            for(World currentWorld : plugin.getServer().getWorlds())
            {
                i++;
                String worldType = currentWorld.getWorldType().getName().toLowerCase();

                Difficulty difficulty = currentWorld.getDifficulty();
                String worldDifficulty = difficulty.name().toLowerCase();
                if(difficulty == Difficulty.PEACEFUL) worldDifficulty = "&b" + worldDifficulty;
                else if(difficulty == Difficulty.EASY) worldDifficulty = "&a" + worldDifficulty;
                else if(difficulty == Difficulty.NORMAL) worldDifficulty = "&e" + worldDifficulty;
                else if(difficulty == Difficulty.HARD) worldDifficulty = "&c" + worldDifficulty;

                World.Environment environment = currentWorld.getEnvironment();
                String worldEnvironment = environment.name().toLowerCase();
                if(environment == World.Environment.NETHER) worldEnvironment = "&c" + worldEnvironment;
                else if(environment == World.Environment.THE_END) worldEnvironment = "&dend";
                else if(environment == World.Environment.NORMAL) worldEnvironment = "&a" + worldEnvironment;

                int playersNumber = currentWorld.getPlayers().size();

                MessageUtils.sendColorizedMessage(commandSender, "&3" + i + "&7: &b" + currentWorld.getName() +
                        "&7, type: &e" + worldType +
                        "&7, pl: &e" + playersNumber +
                        "&7, diff: &e" + worldDifficulty +
                        "&7, env: &e" + worldEnvironment);
            }

            MessageUtils.sendColorizedMessage(commandSender, "&7---------");
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.WORLD_LIST.permission);
            commandSender.sendMessage(errorMessage);
        }
        return true;
    }
}
