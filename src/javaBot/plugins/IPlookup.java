package javaBot.plugins;

// ~--- non-JDK imports --------------------------------------------------------

import java.net.InetAddress;
import java.net.UnknownHostException;

import javaBot.Commands;
import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
/** Looks up IP addresses **/
public class IPlookup extends javaBotPluginAbstract {
	static JavaBot	bot;
	static String	message;
	static String	channel;
	static String	sender;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {
		IPlookup.bot = bot;
		IPlookup.message = message;
		IPlookup.channel = channel;
		IPlookup.sender = sender;
	}

	@Override
	public void run() {
		if (IPlookup.message.startsWith(JavaBot.getPrefix() + "iplookup ")) {
			// TODO Auto-generated method stub
			final String server = Commands.checkParameter(IPlookup.message)[0];

			try {
				final InetAddress[] addresses = InetAddress
				        .getAllByName(server);
				final StringBuffer servers = new StringBuffer("");

				for (final InetAddress ip : addresses) {
					servers.append(ip.toString());
					servers.append(" | ");
				}

				IPlookup.bot.notice(IPlookup.sender, servers.toString());
			}
			catch (final UnknownHostException e) {
				IPlookup.bot.notice(IPlookup.sender, "Unknown host.");
			}
		}
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
