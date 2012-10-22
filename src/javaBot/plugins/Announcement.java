package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.jibble.pircbot.User;

@PluginImplementation

/** An irritating but useful plugin that allows users to announce something or leave a message for another person */
public class Announcement extends javaBotPluginAbstract {
    public void onStart() {
        pluginHelp.addEntry("announce", "announce [message]",
                            "Highlight everyone in the channel and send out your message. Requires authenciation.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.message = message;
        this.channel = channel;
        this.sender  = sender;
    }

    @Override
    public void run() {
        if (matchesStartReference("announce")) {
        	if (isSenderAuthenciated()) {
        		final StringBuffer announcement = new StringBuffer("");

        		for (int i = 0; i < Commands.checkParameter(message).length; i++) {
        			announcement.append(checkParameter(i));
        			announcement.append(" ");
        		}

        		final StringBuffer users      = new StringBuffer("");
        		final User         userList[] = bot.getUsers(channel);

        		for (final User user : userList) {
        			users.append(user.getNick());
        			users.append(" ");
        		}

        		bot.sendMessage(channel, users.toString() + ": " + sender + " is announcing: " + announcement.toString());
        	}
        	else {
        		notEnoughStatus();
        	}
        }
    }
}

//~ Formatted by Jindent --- http://www.jindent.com
