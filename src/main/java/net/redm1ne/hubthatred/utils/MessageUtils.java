package net.redm1ne.hubthatred.utils;


import net.redm1ne.hubthatred.utils.files.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public class MessageUtils
{
    private static final Debugger debugger = new Debugger(MessageUtils.class.getName());

    public static void sendLocalizedMessage(CommandSender sender, LocalizedMessage messageEnum)
    {
        if(sender != null) sender.sendMessage(getLocalizedMessage(messageEnum, true));
        else debugger.sendDebugMessage(Level.SEVERE, "Sender is null!");
    }


    public static void sendColorizedMessage(CommandSender sender, String message)
    {
        if(sender != null) sender.sendMessage(colorize(message));
        else debugger.sendDebugMessage(Level.SEVERE, "Sender is null!");
    }

    public static String getLocalizedMessage(LocalizedMessage messageEnum, boolean applyColor)
    {
        String path = messageEnum.path;

        YamlConfiguration langFile = FileUtils.FileType.LANG_YAML.yaml;

        String localizedMessage = langFile.getString(LocalizedMessage.INFO_PREFIX.path) + langFile.getString(path);

        if (localizedMessage != null)
        {
            if(applyColor)
            {
                localizedMessage = colorize(localizedMessage);
            }

        } else
        {
            debugger.sendDebugMessage(Level.SEVERE, "String " + path + " is null!");
        }
        return localizedMessage;
    }

    public static String colorize(String str)
    {
        str = str.replace('&', '§');
        ChatColor.translateAlternateColorCodes('§', str);
        return str;
    }
}
