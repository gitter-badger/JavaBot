package javaBot;

//~--- non-JDK imports --------------------------------------------------------

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javaBot.plugins.intl.javaBotPlugin;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import org.jibble.pircbot.User;

public class Commands {
    public static final JSPFProperties props = new JSPFProperties();
    public static final PluginManager  pm    = PluginManagerFactory.createPluginManager(props);
    static JavaBot                     bot;

    // HELP VARIABLES \\
    String                    channel;
    String                    message;
    Collection<javaBotPlugin> plugins;
    String                    sender;

    public Commands() {
        props.setProperty(PluginManager.class, "cache.enabled", "true");
        props.setProperty(PluginManager.class, "cache.mode", "weak");
        props.setProperty(PluginManager.class, "cache.file", "jspf.cache");

        PluginManager pm = PluginManagerFactory.createPluginManager(props);

        pm.addPluginsFrom(new File("plugins/").toURI());
        plugins    = new PluginManagerUtil(pm).getPlugins(javaBotPlugin.class);
    }
    
    public void updateVariables(JavaBot bot, String sender, String channel, String message) {
        Commands.bot = bot;
        this.channel = channel;
        this.sender  = sender;
        this.message = message.trim();
    }

    public void run() {
        if (this.message.startsWith(JavaBot.getPrefix())) { // commands
            if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "help")) {
            	pluginHelp.msgReference(bot, this.sender);
                // Ending message
                Commands.bot.notice(this.sender,
                                    "All commands must have the prefix '" + JavaBot.getPrefix()
                                    + "' infront of the command.");
            } else if (this.message.startsWith(JavaBot.getPrefix() + "help ")) {
                final String command = Commands.checkParameter(this.message)[0];
                pluginHelp.msgSpecific(bot, this.sender, command);
            }

            /** PLUGINS */
            for (final javaBotPlugin plugin : this.plugins) {
                plugin.init(Commands.bot, this.message, this.channel, this.sender);
                plugin.run();
            }
        }
        else if (this.message.startsWith(JavaBot.getBotName(bot))) { 
        	// short messages usually used for debugging.
        	if (this.message.endsWith("ping")) {
        		bot.sendMessage(this.channel, this.sender + ": pong");
        	}
        }
        
        System.out.println(JavaBot.getBotName(bot));
    }

    /** Checks parameters of a message. */
    public static String[] checkParameter(String string) {
        final String[]          results1 = string.split(" ");
        final ArrayList<String> results2 = new ArrayList<String>();

        for (int i = 1; i < results1.length; i++) {    
        	// Makes sure that the first entry, the command itself, is removed
            results2.add(results1[i]);
        }

        return results2.toArray(new String[results2.size()]);
    }

    /** Obtain the status of a user or his prefix. */
    String getPrefix(String nickname, String channel) {
        String     status     = "";
        final User userList[] = Commands.bot.getUsers(channel);

        for (final User user : userList) {
            if (user.getNick().equals(nickname)) {
                status = user.getPrefix();

                break;
            }
        }

        return status;
    }

    /**
     * Default message to notify users that an operation is not permitted as
     * they do not have enough status
     */
    public static void notEnoughStatus(String sender) {
        Commands.bot.notice(
            sender,
            "Operation not permitted; you are not authenciated. Type #login USER PASSWORD to authenciate yourself. USER and PASSWORD are case sensitive.");
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
