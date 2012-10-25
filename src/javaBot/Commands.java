package javaBot;

//~--- non-JDK imports --------------------------------------------------------

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
    private static String[]   references;
    private static String[]   syntax;

    // HELP VARIABLES \\

    private static String[]   texts;
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
        texts      = pluginHelp.getTexts();
        references = pluginHelp.getReferences();
        syntax     = pluginHelp.getSyntax();
    }
    
    public void updateVariables(JavaBot bot, String sender, String channel, String message) {
        Commands.bot = bot;
        this.channel = channel;
        this.sender  = sender;
        this.message = message;
    }

    public void run() {
        if (this.message.startsWith(JavaBot.getPrefix())) {
            if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "help")) {
                int          counter = 0;
                StringBuffer string  = new StringBuffer("");

                for (int i = 0; i < references.length; i++) {
                    string.append(references[i]);
                    string.append(" ");
                    counter++;

                    if (counter == 5) {    // once it reaches 5 "references"
                        Commands.bot.notice(this.sender, string.toString());
                        string  = new StringBuffer("");
                        counter = 0;       // reset
                    }
                }

                // Ending message
                Commands.bot.notice(this.sender,
                                    "All commands must have the prefix '" + JavaBot.getPrefix()
                                    + "' infront of the command.");
            } else if (this.message.startsWith(JavaBot.getPrefix() + "help ")) {
                final String command = Commands.checkParameter(this.message)[0];
                List<String> list    = Arrays.asList(references);

                if (list.contains(command)) {    // if the command is inside references
                    int index = list.indexOf(command);

                    Commands.bot.notice(this.sender, command + " | " + syntax[index] + " | " + texts[index]);
                } else {
                    Commands.bot.notice(this.sender, "The command \"" + command + "\" cannot be found.");
                }
            }

            /** PLUGINS */
            for (final javaBotPlugin plugin : this.plugins) {
                plugin.init(Commands.bot, this.message, this.channel, this.sender);
                plugin.run();
            }
        }
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
