package javaBot;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import javaBot.plugins.intl.javaBotPlugin;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import org.jibble.pircbot.User;

public class Commands {
	int	                      a;
	static JavaBot	          bot;
	String	                  channel;
	String	                  hostname;
	String	                  login;
	String	                  message;
	String	                  sender;
	boolean	                  training;

	PluginManager	          pm;

	Collection<javaBotPlugin>	plugins;

	public Commands(JavaBot bot, String sender, String login, String channel,
	        String hostname, String message) throws MalformedURLException {
		Commands.bot = bot;
		this.channel = channel;
		this.sender = sender;
		this.hostname = hostname;
		this.message = message;
		this.login = login;

		final JSPFProperties props = new JSPFProperties();

		props.setProperty(PluginManager.class, "cache.enabled", "true");
		props.setProperty(PluginManager.class, "cache.file", ".jspf.cache");

		this.pm = PluginManagerFactory.createPluginManager(props);

		this.pm.addPluginsFrom(new File("plugins/").toURI());
		this.plugins = new PluginManagerUtil(this.pm)
		        .getPlugins(javaBotPlugin.class);
	}

	public void run() {

		if (this.message.startsWith(JavaBot.getPrefix())) {

			if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "quit")) {
				if (JavaBot.AUTHENCIATED.contains(this.sender)
				        || Authenciation.checkNoUsers()) {
					Commands.bot.disconnect();
				}
				else {
					Commands.notEnoughStatus(this.sender);
				}
			}

			else if (this.message.startsWith(JavaBot.getPrefix() + "join ")) {
				final String parameter = Commands.checkParameter(this.message)[0];

				if (JavaBot.AUTHENCIATED.contains(this.sender)
				        || Authenciation.checkNoUsers()) {
					Commands.bot.joinChannel(parameter);
				}
				else {
					Commands.notEnoughStatus(this.sender);
				}
			}

			else if (this.message
			        .equalsIgnoreCase(JavaBot.getPrefix() + "part")) {
				if (JavaBot.AUTHENCIATED.contains(this.sender)
				        || Authenciation.checkNoUsers()) {
					Commands.bot.partChannel(this.channel, this.sender);
				}
				else {
					Commands.notEnoughStatus(this.sender);
				}
			}

			else if (this.message.startsWith(JavaBot.getPrefix() + "part ")) {
				final String parameter = Commands.checkParameter(this.message)[0];
				if (JavaBot.AUTHENCIATED.contains(this.sender)
				        || Authenciation.checkNoUsers()) {
					Commands.bot.partChannel(parameter, this.sender);
				}
				else {
					Commands.notEnoughStatus(this.sender);
				}
			}

			else if (this.message.startsWith(JavaBot.getPrefix() + "tell ")) {
				final String user = Commands.checkParameter(this.message)[0];
				final String messageTold = Commands.checkParameter(this.message)[1];
				if (JavaBot.AUTHENCIATED.contains(this.sender) || Authenciation.checkNoUsers()) {
					Commands.bot.sendMessage(user, messageTold);
				}
				else {
					Commands.notEnoughStatus(this.sender);
				}
			}

			/** HELP commands - MANTAIN THEM WELL */
			else if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "help")) {
				// Ending message
				Commands.bot.notice(
					this.sender,
				    "All commands must have the prefix '" + 
					JavaBot.getPrefix() +
					"' infront of the command.");
			}
			else if (this.message.startsWith(JavaBot.getPrefix() + "help ")) {
				final String command = Commands.checkParameter(this.message)[0];

				//TODO To be implemented
			}

			/** PLUGINS */
			for (final javaBotPlugin plugin : this.plugins) {
				plugin.init(Commands.bot, this.message, this.channel, this.sender);
				plugin.run();
			}
		}
	}

	/** Checks parameters of a message. **/
	public static String[] checkParameter(String string) {
		final String[] results1 = string.split(" ");

		final ArrayList<String> results2 = new ArrayList<String>();
		for (int i = 1; i < results1.length; i++) { // Makes sure that the first
			                                        // entry, the command
			                                        // itself, is removed
			results2.add(results1[i]);
		}

		return results2.toArray(new String[results2.size()]);
	}

	/** Obtain the status of a user or his prefix. **/
	String getPrefix(String nickname, String channel) {
		String status = "";
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
	 **/
	public static void notEnoughStatus(String sender) {
		Commands.bot
		        .notice(sender,
		                "Operation not permitted; you are not authenciated. Type #login USER PASSWORD to authenciate yourself. USER and PASSWORD are case sensitive.");
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
