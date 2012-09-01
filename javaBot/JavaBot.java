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
		}
		catch (Exception e) {
			log(e.getStackTrace().toString());
		}

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

		JavaBot.CHANNEL_ARRAY = JavaBot.CHANNEL.split(" ");
	}

	@Override
	public void run() {

		final JavaBot bot = new JavaBot();

		bot.setName(JavaBot.NAME);
		bot.setMessageDelay(JavaBot.DELAY);    // Set message delay.

		bot.setVerbose(true);

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
				log(e.getStackTrace().toString());;
			}
		}
		catch (final Exception e) {
			log(e.getStackTrace().toString());;
		}

		bot.identify(JavaBot.PASSWORD);
		
		bot.sendRawLine("MODE "+JavaBot.getBotName()+" +B");
		
		try {
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1) {
			log(e1.getStackTrace().toString());;
		}

		for (final String element : JavaBot.CHANNEL_ARRAY) {
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
				log(e.getStackTrace().toString());;
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
				log(e.getStackTrace().toString());;
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
					log(e.getStackTrace().toString());;
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
					log(e.getStackTrace().toString());;
				}

				this.setMode(CHANNEl, "-il");
			}
		}
	}

	@Override
	protected void onDisconnect() {
		// Cleaning up

		System.exit(0);
	}

	
	/**
	 * Sends a notice if the target is in a channel the bot 
	 * is in. Or else, send a private message so that no 
	 * message will be lost.
	 * 
	 * The reason for this implementation is that
	 * the function NOTICE only works if the user
	 * is in the same channel.
	 * 
	 * @param sender Sender of message.
	 * @param message Message to be sent.
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
			this.sendNotice(sender, message);
		}
		else {
			this.sendMessage(sender, message);
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
