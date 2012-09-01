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

	private static String CHANNEL = "";
	private static long DELAY = 0;
	private static String NAME = "";
	private static String PASSWORD = "";
	private static String PREFIX = "";
	private static String SERVER = "";
	static String[] CHANNEL_ARRAY;

	protected static ArrayList<String>	AUTHENCIATED = new ArrayList<String>();

	static boolean CYCLE = false;

	private static long	FLOOD_DURATION = 0;
	private static long	THROTTLED_TIME = 0;
	private static long	MESSAGE_LIMIT = 0;

	private static boolean PROTECT_MODE = false;

	private static long AUTHENCIATION_DELAY = 0;

	String line	              = null;
	static ArrayList<String>	               configArray	      = new ArrayList<String>();
	static ArrayList<String>	               configNameArray	  = new ArrayList<String>();
	File config	          = new File("files/config.txt");
	BufferedReader configIn;
	BufferedReader in;

	protected JavaBot() {
		try {

			this.configIn = new BufferedReader(new FileReader(this.config));
			this.line = this.configIn.readLine();

			while (this.line != null) {
				final String[] array = this.line.split("=");

				if (array.length == 2) {
					JavaBot.configNameArray.add(array[0].replaceAll("=", "").trim());
					JavaBot.configArray.add(array[1].trim());
				}

				this.line = this.configIn.readLine();
			}

			try {
				JavaBot.NAME = JavaBot.getConfig("nick");
				JavaBot.SERVER = JavaBot.getConfig("server");
				JavaBot.CHANNEL = JavaBot.getConfig("channels");
				JavaBot.PASSWORD = JavaBot.getConfig("password");
				JavaBot.PREFIX = JavaBot.getConfig("prefix");
				JavaBot.DELAY = Long.parseLong(JavaBot.getConfig("messageDelay"));

				JavaBot.FLOOD_DURATION = Long.parseLong(JavaBot.getConfig("floodDuration"));
				JavaBot.THROTTLED_TIME = Long.parseLong(JavaBot.getConfig("throttledTime"));
				JavaBot.MESSAGE_LIMIT = Long.parseLong(JavaBot.getConfig("messageLimit"));

				JavaBot.PROTECT_MODE = Boolean.parseBoolean(JavaBot.getConfig("protectMode"));

				JavaBot.AUTHENCIATION_DELAY = Long.parseLong(JavaBot.getConfig("authenciationDelay"));
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

		/** CHANNEls to join */
		JavaBot.CHANNEL_ARRAY = JavaBot.CHANNEL.split(" ");
	}

	@Override
	public void run() {

		// Now start our bot up.
		final JavaBot bot = new JavaBot();

		// Set options
		bot.setName(JavaBot.NAME);
		bot.setMessageDelay(JavaBot.DELAY);    // Set message delay.

		// Set debugging
		bot.setVerbose(true);

		// Connect to the IRC server.
		try {
			bot.connect(JavaBot.SERVER);
		}
		catch (final NickAlreadyInUseException e) {
			JavaBot.NAME = JavaBot.NAME + Math.abs(Generator.generateInt());
			bot.setName(JavaBot.NAME);
			try {
				bot.connect(JavaBot.SERVER);
			}
			catch (final Exception e2) {
				e.printStackTrace();
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		// Identify the bot.
		bot.identify(JavaBot.PASSWORD);
		
		// Adds botmode
		bot.sendRawLine("MODE "+JavaBot.getBotName()+" +B");
		
		// Delay of a second before joining CHANNEls.
		try {
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1) {
			e1.printStackTrace();
		}

		for (final String element : JavaBot.CHANNEL_ARRAY) {
			// Join the CHANNEls.
			bot.joinChannel(element);
		}
	}

	@Override
	protected void onMessage(String CHANNEl, String sender, String login, String hostname, 
		String message) {
		if (message.startsWith(JavaBot.getPrefix())) {
			try {
				new Commands(this, sender, login, CHANNEl, hostname, message).run();
			}
			catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		}

		new Security(this, CHANNEl, sender, message, hostname).run();
		new FloodPreventor(this, CHANNEl, sender, message, JavaBot.FLOOD_DURATION, 
			JavaBot.MESSAGE_LIMIT, JavaBot.THROTTLED_TIME).run();
	}

	@Override
	protected void onPrivateMessage(String sender, String login,
	        String hostname, String message) {
		if (message.startsWith(JavaBot.getPrefix())) {
			try {
				new Commands(this, sender, login, sender, hostname, message).run();
				new Authenciation(this, sender, message).run();
			}
			catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onKick(String CHANNEl, String kickerNick,
	        String kickerLogin, String kickerHostname, String recipientNick,
	        String reason) {
		if (JavaBot.PROTECT_MODE) {
			if (recipientNick.equals(JavaBot.NAME)
			        && !kickerNick.equals("ChanServ")) {
				this.joinChannel(CHANNEl);

				final StringBuffer kicker = new StringBuffer(kickerHostname);

				kicker.insert(0, "!*@*");
				this.ban(CHANNEl, kicker.toString());
				this.kick(CHANNEl, kickerNick);
			}
		}
	}

	@Override
	protected void onSetChannelBan(String CHANNEl, String sourceNick,
	        String sourceLogin, String sourceHostname, String hostmask) {
		if (JavaBot.PROTECT_MODE) {
			if (hostmask.equals("*!*@*") && !sourceNick.equals("ChanServ")) {

				final StringBuffer kicker = new StringBuffer(sourceHostname);

				kicker.insert(0, "!*@*");
				this.ban(CHANNEl, kicker.toString());
				this.kick(CHANNEl, sourceNick);
				this.unBan(CHANNEl, "*!*@*");

				try {
					Thread.sleep(1000);
				}
				catch (final InterruptedException e) {
					e.printStackTrace();
				}

				this.setMode(CHANNEl, "-il");
			}
			else if (hostmask.contains("JavaBot")
			        && !sourceNick.equals("ChanServ")) {

				this.sendMessage("ChanServ", "recover " + CHANNEl);

				final StringBuffer kicker = new StringBuffer(sourceHostname);

				kicker.insert(0, "!*@*");
				this.ban(CHANNEl, kicker.toString());
				this.kick(CHANNEl, sourceNick);
				try {
					Thread.sleep(1000);
				}
				catch (final InterruptedException e) {
					e.printStackTrace();
				}

				this.setMode(CHANNEl, "-il");
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
	 * CHANNEl with the same user which means the user will obtain the
	 * notification in the CHANNEl itself. If the user is not in the CHANNEls
	 * the bot is in, the bot sends a private message instead.
	 */
	public void notice(String sender, String message) {
		final String[] CHANNEls = JavaBot.CHANNEL.split(" ");

		boolean found = false;
		for (final String CHANNEl2 : CHANNEls) {
			final User[] userList = this.getUsers(CHANNEl2);
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
	
	private static String getConfig(String parameter) {
		if (JavaBot.configNameArray.contains(parameter)) { 
			return JavaBot.configArray.get(JavaBot.configNameArray.indexOf(parameter));
		}
		else {
			throw new java.lang.NullPointerException(parameter + " not found in configNameArray. " + 
				"\n" +"Please recheck the config file.");
		}
	}

	// Getter methods
	public static String getBotName() {
		return JavaBot.NAME;
	}

	public static String getCHANNEl() {
		return JavaBot.CHANNEL;
	}

	public static long getDelay() {
		return JavaBot.DELAY;
	}

	public static String getPrefix() {
		return JavaBot.PREFIX;
	}

	public static String getServerName() {
		return JavaBot.SERVER;
	}

	protected static long getAuthenciationDelay() {
		return JavaBot.AUTHENCIATION_DELAY;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
