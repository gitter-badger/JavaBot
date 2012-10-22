package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class Basic extends javaBotPluginAbstract {
    public void onStart() {
        pluginHelp.addEntry("quit", "quit", "Disconnects from the server.");
        pluginHelp.addEntry("join", "join [channel]", "Joins a channel.");
        pluginHelp.addEntry("part", "part | part [channel]",
                            "Parts a channel. Default is channel where the command was executed.");
        pluginHelp.addEntry("tell", "tell [user] [message]", "Tell a user a message.");
    }

    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.message = message;
        this.channel = channel;
        this.sender  = sender;
    }

    public void run() {
        if (matchesReference("quit")) {
            if (isSenderAuthenciated()) {
                bot.disconnect();
            } else {
                notEnoughStatus();
            }
        } else if (matchesStartReference("join")) {
            final String parameter = checkParameter(0);

            if (isSenderAuthenciated()) {
                bot.joinChannel(parameter);
            } else {
                notEnoughStatus();
            }
        } else if (matchesReference("part")) {
            if (isSenderAuthenciated()) {
                bot.partChannel(this.channel, this.sender);
            } else {
                notEnoughStatus();
            }
        } else if (matchesStartReference("part")) {
            final String parameter = checkParameter(0);

            if (isSenderAuthenciated()) {
                bot.partChannel(parameter, this.sender);
            } else {
                notEnoughStatus();
            }
        } else if (matchesStartReference("tell")) {
            final String user        = checkParameter(0);
            final String messageTold = checkParameter(1);

            if (isSenderAuthenciated()) {
                bot.sendMessage(user, messageTold);
            } else {
                notEnoughStatus();
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
