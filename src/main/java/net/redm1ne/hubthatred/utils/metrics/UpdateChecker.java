package net.redm1ne.hubthatred.utils.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.redm1ne.hubthatred.HubThatRed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import net.redm1ne.hubthatred.utils.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;

public class UpdateChecker implements Runnable
{

    public static BukkitTask task;

    private final Debugger debugger = new Debugger(getClass().getName());

    public String newVersion, updateLink;
    public ArrayList<String> updateText = new ArrayList<>(), warningMessage = new ArrayList<>();
    public Boolean updateWarningBoolean, isNewVersionOut = false;

    public Boolean isServerUnreachable = true;
    public String errorCode;

    private final HubThatRed plugin;
    public UpdateChecker(HubThatRed givenPlugin)
    {
        plugin = givenPlugin;
    }

    private String servicesUrl = "https://services.mind-overflow.net/";
    private String hubthatUrl = "java/plugins/hubthat/updates/";

    @Override
    public void run()
    {
        CommandSender console = plugin.getServer().getConsoleSender();
        updateText.clear();
        warningMessage.clear();


        try
        {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(servicesUrl).openConnection();
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();

            if (responseCode != 200)
            {
                setAndSendErrorCode(responseCode + "", console);
                return;
            }

            errorCode = null;


        }
        catch (IOException e)
        {
            sendUnreachableCode(console);
            isServerUnreachable = true;
            return;
        }
        isServerUnreachable = false;

        InputStream jsonIS;
        try
        {

            jsonIS = new URL( servicesUrl + hubthatUrl + "update.json").openStream();

        } catch (IOException e)
        {
            setAndSendErrorCode(e.getMessage(), console);
            return;
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject json = (JsonObject)jsonParser.parse(new InputStreamReader(jsonIS, StandardCharsets.UTF_8));

        try {
            jsonIS.close();
        } catch (IOException ignored)
        {}

        newVersion = null;
        if(json.get("version") == null)
        {
            setAndSendErrorCode("null version", console);
            return;
        }

        newVersion = json.get("version").getAsString();

        if(newVersion.equalsIgnoreCase(plugin.getDescription().getVersion()))
        {
            isNewVersionOut = false;
            return;
        }

        isNewVersionOut = true;

        updateLink = null;
        if(json.get("link") == null)
        {
            setAndSendErrorCode("null link", console);
            return;
        }
        updateLink = json.get("link").getAsString();


        if(json.get("text") == null)
        {
            setAndSendErrorCode("null text", console);
            return;
        }
        JsonArray updateTextArray = json.get("text").getAsJsonArray();
        for(JsonElement obj : updateTextArray)
        {
            String relatedString = obj.getAsString();
            updateText.add(relatedString);
        }

        if(json.get("warning") == null)
        {
            setAndSendErrorCode("null warning", console);
            return;
        }
        JsonObject warning = json.get("warning").getAsJsonObject();

        if(warning.get("enabled") == null)
        {
            setAndSendErrorCode("null warning boolean", console);
            return;
        }
        updateWarningBoolean = warning.get("enabled").getAsBoolean();

        if(warning.get("text") == null)
        {
            setAndSendErrorCode("null warning text", console);
            return;
        }
        JsonArray warningTextArray = warning.get("text").getAsJsonArray();
        for(JsonElement obj : warningTextArray)
        {
            String relatedString = obj.getAsString();
            warningMessage.add(relatedString);
        }

        sendUpdateMessages(console);

    }

    public void sendUnreachableCode(CommandSender sender)
    {
        String pluginName = plugin.getName();
        MessageUtils.sendColorizedMessage(sender, "&7-----[&3 " + pluginName + " Updater &7]-----");
        MessageUtils.sendColorizedMessage(sender, "&cWarning! Updates Server is unreachable.");
        MessageUtils.sendColorizedMessage(sender, "&cTry fixing connectivity problems and reload " + pluginName + " with &3/" + pluginName.toLowerCase() + " reload&c!");
    }

    public void setAndSendErrorCode(String code, CommandSender sender)
    {
        isNewVersionOut = false;
        errorCode = code;
        sendErrorCode(sender);
    }

    public void sendErrorCode(CommandSender sender)
    {
        String pluginName = plugin.getName();
        MessageUtils.sendColorizedMessage(sender, "&7-----[&3 " + pluginName + " Updater &7]-----");
        MessageUtils.sendColorizedMessage(sender, "&cWarning! Updates Server returned error code: &4" + errorCode);
        MessageUtils.sendColorizedMessage(sender, "&cPlease contact the developer (" + debugger.authorName + ") immediately.");
    }


    public void sendUpdateMessages(CommandSender sender)
    {
        String pluginName = plugin.getName();
        MessageUtils.sendColorizedMessage(sender, "&7-----[&3 " + pluginName + " Updater &7]-----");
        MessageUtils.sendColorizedMessage(sender, "&7A new version is out: &6" + newVersion);
        MessageUtils.sendColorizedMessage(sender, "&7Download: &6" + updateLink);
        for(String line : updateText)
        {
            MessageUtils.sendColorizedMessage(sender, line);
        }
        if(updateWarningBoolean)
        {
            for(String line : warningMessage)
            {
                MessageUtils.sendColorizedMessage(sender, line);
            }
        }
    }

    public void playerMessage(CommandSender player)
    {
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
        {
            if(!(player instanceof Player)) return;

            if(PluginCache.updateChecker)
            {
                debugger.sendDebugMessage(Level.INFO, "Update Checker is enabled!");
                if(PermissionUtils.playerHasPermission(player, Permissions.GET_UPDATES_NOTIFICATIONS))
                {
                    debugger.sendDebugMessage(Level.INFO, "Player has permissions to check updates.");

                    if(isNewVersionOut)
                    {
                            sendUpdateMessages(player);
                    }

                    if(isServerUnreachable)
                    {
                        sendUnreachableCode(player);
                    }

                    if(errorCode != null)
                    {

                        sendErrorCode(player);
                    }
                }
            }
        }, 40);


    }
}
