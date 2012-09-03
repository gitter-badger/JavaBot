package javaBot.plugins;

import javaBot.Commands;
import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.jibble.pircbot.User;

@PluginImplementation
/** An irritating but useful plugin that allows users to announce something or leave a message for another person **/
public class Announcement extends javaBotPluginAbstract {
	static JavaBot	bot;
	static String	message;
	static String	sender;
	static String	channel;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {
		Announcement.bot = bot;
		Announcement.message = message;
		Announcement.channel = channel;
		Announcement.sender = sender;
	}

	@Override
	public void run() {
		if (Announcement.message.startsWith(JavaBot.getPrefix() + "announce ")) {
			final StringBuffer announcement = new StringBuffer("");

			for (int i = 0; i < Commands.checkParameter(Announcement.message).length; i++) {
				announcement.append(Commands
				        .checkParameter(Announcement.message)[i]);
				announcement.append(" ");
			}

			final StringBuffer users = new StringBuffer("");
			final User userList[] = Announcement.bot
			        .getUsers(Announcement.channel);
			for (final User user : userList) {
				users.append(user.getNick());
				users.append(" ");
			}

			Announcement.bot.sendMessage(Announcement.channel, users.toString()
			        + ": " + Announcement.sender + " is announcing: "
			        + announcement.toString());
		}
	}
}
