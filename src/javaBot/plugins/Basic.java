package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
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
        if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "quit")) {
            if (JavaBot.isAuthenciated(this.sender)) {
                bot.disconnect();
            } else {
                Commands.notEnoughStatus(this.sender);
            }
        } else if (this.message.startsWith(JavaBot.getPrefix() + "join ")) {
            final String parameter = Commands.checkParameter(this.message)[0];

            if (JavaBot.isAuthenciated(this.sender)) {
                bot.joinChannel(parameter);
            } else {
                Commands.notEnoughStatus(this.sender);
            }
        } else if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "part")) {
            if (JavaBot.isAuthenciated(this.sender)) {
                bot.partChannel(this.channel, this.sender);
            } else {
                Commands.notEnoughStatus(this.sender);
            }
        } else if (this.message.startsWith(JavaBot.getPrefix() + "part ")) {
            final String parameter = Commands.checkParameter(this.message)[0];

            if (JavaBot.isAuthenciated(this.sender)) {
                bot.partChannel(parameter, this.sender);
            } else {
                Commands.notEnoughStatus(this.sender);
            }
        } else if (this.message.startsWith(JavaBot.getPrefix() + "tell ")) {
            final String user        = Commands.checkParameter(this.message)[0];
            final String messageTold = Commands.checkParameter(this.message)[1];

            if (JavaBot.isAuthenciated(this.sender)) {
                bot.sendMessage(user, messageTold);
            } else {
                Commands.notEnoughStatus(this.sender);
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
