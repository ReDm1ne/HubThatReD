package net.redm1ne.hubthatred.utils;

import org.bukkit.command.CommandSender;

public class PermissionUtils
{
    public static boolean playerHasPermission(CommandSender user, Permissions permission)
    {
        if (user != null && user.hasPermission(permission.permission))
        {
            return true;
        }

        return false;
    }
}
