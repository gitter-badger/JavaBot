package javaBot;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import wei2912.utilities.Generator;

/**
 * @version 1.3.1
 * @author wei2912
 */

/** JavaBot main class */
public class JavaBot extends PircBot implements Runnable {

	// Constants
	private static String	           channel	          = "";
	private static long	               delay	          = 0;
	private static String	           name	              = "";
	private static String	           password	          = "";
	private static String	           prefix	          = "";
	private static String	           server	          = "";
	static String[]	                   channelArray;

	protected static ArrayList<String>	authenciated	  = new ArrayList<String>();

	// Cycle boolean
	static boolean	                   cycle	          = false;

	// FloodPreventor
	private static long	               floodDuration	  = 0;
	private static long	               throttledTime	  = 0;
	private static long	               messageLimit	      = 0;

	// Protection mode
	private static boolean	           protectMode	      = false;

	// Authenciation delay
	private static long	               authenciationDelay	= 0;

	// Variables for systems
	String	                           line	              = null;
	ArrayList<String>	               configArray	      = new ArrayList<String>();
	ArrayList<String>	               configNameArray	  = new ArrayList<String>();
	File	                           config	          = new File(
	                                                              "files/config.txt");
	BufferedReader	                   configIn;
	BufferedReader	                   in;

	protected JavaBot() {
		try {

			/** Config */
			this.configIn = new BufferedReader(new FileReader(this.config));
			this.line = this.configIn.readLine();

			while (this.line != null) {
				final String[] array = this.line.split("=");

				if (array.length == 2) {
					this.configNameArray.add(array[0].replaceAll("=", "")
					        .trim());
					this.configArray.add(array[1].trim());
				}

				this.line = this.configIn.readLine();
			}

			try {
				JavaBot.name = this.configArray.get(this.configNameArray
				        .indexOf("nick"));
				JavaBot.server = this.configArray.get(this.configNameArray
				        .indexOf("server"));
				JavaBot.channel = this.configArray.get(this.configNameArray
				        .indexOf("channels"));
				JavaBot.password = this.configArray.get(this.configNameArray
				        .indexOf("password"));
				JavaBot.prefix = this.configArray.get(this.configNameArray
				        .indexOf("prefix"));
				JavaBot.delay = Long.parseLong(this.configArray
				        .get(this.configNameArray.indexOf("messageDelay")));

				JavaBot.floodDuration = Long.parseLong(this.configArray
				        .get(this.configNameArray.indexOf("floodDuration")));
				JavaBot.throttledTime = Long.parseLong(this.configArray
				        .get(this.configNameArray.indexOf("throttledTime")));
				JavaBot.messageLimit = Long.parseLong(this.configArray
				        .get(this.configNameArray.indexOf("messageLimit")));

				JavaBot.protectMode = Boolean.parseBoolean(this.configArray
				        .get(this.configNameArray.indexOf("protectMode")));

				JavaBot.authenciationDelay = Long
				        .parseLong(this.configArray.get(this.configNameArray
				                .indexOf("authenciationDelay")));
			}
			catch (final Exception e) {
				e.printStackTrace();
				System.out.println("ERROR: Please recheck the config file.");
				System.exit(0);
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		/** Channels to join */
		JavaBot.channelArray = JavaBot.channel.split(" ");
	}

	@Override
	public void run() {

		// Now start our bot up.
		final JavaBot bot = new JavaBot();

		// Set options
		bot.setName(JavaBot.name);
		bot.setMessageDelay(JavaBot.delay);    // Set message delay.

		// Set debugging
		bot.setVerbose(true);

		// Connect to the IRC server.
		try {
			bot.connect(JavaBot.server);
		}
		catch (final NickAlreadyInUseException e) {
			JavaBot.name = JavaBot.name + Math.abs(Generator.generateInt());
			bot.setName(JavaBot.name);
			try {
				bot.connect(JavaBot.server);
			}
			catch (final Exception e2) {
				e.printStackTrace();
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		// Identify the bot.
		bot.identify(JavaBot.password);
		
		// Adds botmode
		bot.sendRawLine("MODE "+JavaBot.getBotName()+" +B");
		
		// Delay of a second before joining channels.
		try {
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1) {
			e1.printStackTrace();
		}

		for (final String element : JavaBot.channelArray) {
			// Join the channels.
			bot.joinChannel(element);
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (message.startsWith(JavaBot.getPrefix())) {
			try {
				new Thread(new Commands(this, sender, login, channel, hostname,
				        message)).start();
			}
			catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		}

		new Thread(new Security(this, channel, sender, message, hostname))
		        .start();
		new Thread(new FloodPreventor(this, channel, sender, message,
		        JavaBot.floodDuration, JavaBot.messageLimit,
		        JavaBot.throttledTime)).start();
	}

	@Override
	protected void onPrivateMessage(String sender, String login,
	        String hostname, String message) {
		if (message.startsWith(JavaBot.getPrefix())) {
			try {
				new Thread(new Commands(this, sender, login, sender, hostname,
				        message)).start();
				new Thread(new Authenciation(this, sender, message)).start();
			}
			catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onKick(String channel, String kickerNick,
	        String kickerLogin, String kickerHostname, String recipientNick,
	        String reason) {
		if (JavaBot.protectMode) {
			if (recipientNick.equals(JavaBot.name)
			        && !kickerNick.equals("ChanServ")) {
				this.joinChannel(channel);

				// For a better ban
				final StringBuffer kicker = new StringBuffer(kickerHostname);

				kicker.insert(0, "!*@*");
				this.ban(channel, kicker.toString());
				this.kick(channel, kickerNick);
			}
		}
	}

	@Override
	protected void onSetChannelBan(String channel, String sourceNick,
	        String sourceLogin, String sourceHostname, String hostmask) {
		if (JavaBot.protectMode) {
			if (hostmask.equals("*!*@*") && !sourceNick.equals("ChanServ")) {

				// For a better ban
				final StringBuffer kicker = new StringBuffer(sourceHostname);

				kicker.insert(0, "!*@*");
				this.ban(channel, kicker.toString());
				this.kick(channel, sourceNick);
				this.unBan(channel, "*!*@*");

				try {
					Thread.sleep(1000);
				}
				catch (final InterruptedException e) {
					e.printStackTrace();
				}

				this.setMode(channel, "-il");
			}
			else if (hostmask.contains("JavaBot")
			        && !sourceNick.equals("ChanServ")) {

				// For a better ban
				this.sendMessage("ChanServ", "recover " + channel);

				final StringBuffer kicker = new StringBuffer(sourceHostname);

				kicker.insert(0, "!*@*");
				this.ban(channel, kicker.toString());
				this.kick(channel, sourceNick);
				try {
					Thread.sleep(1000);
				}
				catch (final InterruptedException e) {
					e.printStackTrace();
				}

				this.setMode(channel, "-il");
			}
		}
	}

	/**
	 * On disconnect, exit. Prevents floating instance of bot.
	 */
	@Override
	protected void onDisconnect() {
		// Cleaning up

		System.exit(0);
	}

	/**
	 * Wrapper method to send notices. It will send notices when it is in a
	 * channel with the same user which means the user will obtain the
	 * notification in the channel itself. If the user is not in the channels
	 * the bot is in, the bot sends a private message instead.
	 */
	public void notice(String sender, String message) {
		final String[] channels = JavaBot.channel.split(" ");

		boolean found = false;
		for (final String channel2 : channels) {
			final User[] userList = this.getUsers(channel2);
			for (final User user : userList) {
				if (user.getNick().equals(sender)) {
					found = true;
					break;
				}
			}

			if (found == true) {
				break;
			}
		}

		if (found == true) {
			this.sendNotice(sender, message); // Sends a notice
		}
		else {
			this.sendMessage(sender, message); // Sends a private message
		}
	}

	// Getter methods
	public static String getBotName() {
		return JavaBot.name;
	}

	public static String getChannel() {
		return JavaBot.channel;
	}

	public static long getDelay() {
		return JavaBot.delay;
	}

	public static String getPrefix() {
		return JavaBot.prefix;
	}

	public static String getServerName() {
		return JavaBot.server;
	}

	protected static long getAuthenciationDelay() {
		return JavaBot.authenciationDelay;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
