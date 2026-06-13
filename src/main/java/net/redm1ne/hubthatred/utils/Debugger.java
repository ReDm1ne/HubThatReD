package net.redm1ne.hubthatred.utils;

import net.redm1ne.hubthatred.HubThatRed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

public class Debugger
{
    private final boolean DEBUGGING = false;

    private String className;
    private String packageName;

    public UUID authorUUID = UUID.fromString("297a1dc8-c0a3-485a-ad21-8956c749f927");
    public String authorName = "mind_overflow";

    public Debugger(String instanceClassName)
    {
        if(DEBUGGING)
        {
            Class instanceClass = getClass();
            try
            {
                instanceClass = Class.forName(instanceClassName);
            }
            catch (ClassNotFoundException e)
            {
                HubThatRed.logger.log(Level.INFO, "WTF? A class made an instance of the Debugger but it somehow can't define which class was it. Very weird. Setting it to the Debugger class.");
                HubThatRed.logger.log(Level.INFO, "Please send the following error log to me (" + authorName + "):");
                e.printStackTrace();
            }
            className = instanceClass.getSimpleName();
            packageName = instanceClass.getPackage().getName();
        }
    }

    public void sendDebugMessage(Level lvl, String str)
    {
        if(DEBUGGING)
        {
            String msg = className + ": " + str;

            HubThatRed.logger.log(lvl, msg);

            Player author = Bukkit.getPlayer(authorUUID);
            if(author == null) {
                author = Bukkit.getPlayer(authorName);
            }
            if(author != null)
            {
                if(Bukkit.getServer().getOnlinePlayers().contains(author))
                {
                    author.sendMessage(msg);
                }
            }
        }
    }
}
