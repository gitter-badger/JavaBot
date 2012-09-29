package javaBot;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import org.jibble.pircbot.IrcException;
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

	// CONFIG VARIABLES \\
	private static String CHANNEL = "";
	private static long DELAY = 0;
	private static String NAME = "";
	private static String NICKSERV_PASSWORD = "";
	private static String SERVER_PASSWORD = "";
	private static String PREFIX = "";
	private static String SERVER = "";
	
	private static int PORT = 6667;
	private static boolean SSL = false;

	private static boolean PROTECT_MODE = false;
	
	private static long AUTHENCIATION_DELAY = 0;
	// CONFIG VARIABLES \\
	
	protected static ArrayList<String>	AUTHENCIATED = new ArrayList<String>();
	static String[] CHANNEL_ARRAY;
	static boolean CYCLE = false;
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
			logException(e);
		}

		JavaBot.NAME = JavaBot.getConfig("nick");
		JavaBot.SERVER = JavaBot.getConfig("server");
		JavaBot.CHANNEL = JavaBot.getConfig("channels");
		JavaBot.NICKSERV_PASSWORD = JavaBot.getConfig("nickserv_password");
		
		JavaBot.SERVER_PASSWORD = JavaBot.getConfig("server_password");
		
		JavaBot.PREFIX = JavaBot.getConfig("prefix");
		JavaBot.DELAY = Long.parseLong(JavaBot.getConfig("messageDelay"));
		
		JavaBot.PORT = Integer.parseInt(JavaBot.getConfig("port"));
		JavaBot.SSL = Boolean.parseBoolean(JavaBot.getConfig("sslConnection"));

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
			bot.connectServer();
		}
		catch (final NickAlreadyInUseException e) {
			JavaBot.NAME = JavaBot.NAME + Math.abs(Generator.generateInt(1000,9999));
			bot.setName(JavaBot.NAME);
			try {
				bot.connectServer();
			}
			catch (final Exception e2) {
				logException(e2);
			}
		}
		catch (final Exception e) {
			logException(e);
		}

		bot.identify(JavaBot.NICKSERV_PASSWORD);
		
		bot.sendRawLine("MODE "+JavaBot.getBotName()+" +B");
		
		try {
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1) {
			logException(e1);
		}

		for (final String element : JavaBot.CHANNEL_ARRAY) {
			bot.joinChannel(element);
		}
	}
	
	public void connectServer() throws SSLException, IOException, IrcException {
		if (JavaBot.SSL) {
			if (JavaBot.SERVER_PASSWORD.equalsIgnoreCase("null")) {
				this.connect(JavaBot.SERVER, JavaBot.PORT, null, SSLSocketFactory.getDefault());
			}
			else {
				this.connect(JavaBot.SERVER, JavaBot.PORT, JavaBot.SERVER_PASSWORD, SSLSocketFactory.getDefault());
			}
		}
		else {
			if (JavaBot.SERVER_PASSWORD.equalsIgnoreCase("null")) {
				this.connect(JavaBot.SERVER, JavaBot.PORT, null, null);
			}
			else {
				this.connect(JavaBot.SERVER, JavaBot.PORT, JavaBot.SERVER_PASSWORD, null);
			}
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, 
		String message) {
		if (message.startsWith(JavaBot.getPrefix())) {
			try {
				new Commands(this, sender, login, channel, hostname, message).run();
			}
			catch (final MalformedURLException e) {
				logException(e, sender);
			}
		}

		new Security(this, channel, sender, message, hostname).run();
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
				logException(e, sender);
			}
		}
	}

	@Override
	protected void onKick(String channel, String kickerNick,
	        String kickerLogin, String kickerHostname, String recipientNick,
	        String reason) {
		
		if (recipientNick.equals(JavaBot.NAME) && 
			!kickerNick.equals("ChanServ")) {
			this.joinChannel(channel);

			if (JavaBot.PROTECT_MODE) {
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
		
		String newhostmask = hostmask.replace("*!*", ".*");
			   newhostmask = newhostmask.replaceAll("[^\\.]*", "\\.*");
			
		if (("*!*@JavaBot".matches(newhostmask) ||
			 "*!*PircBotPPF@JavaBot".matches(newhostmask)) && 
			!sourceNick.equals("ChanServ")) {

			if (JavaBot.PROTECT_MODE) {
				final StringBuffer kicker = new StringBuffer(sourceHostname);

				kicker.insert(0, "	");
				this.ban(channel, kicker.toString());
				this.kick(channel, sourceNick);
			}
				
			this.unBan(channel, hostmask);
		}
	}

	@Override
	protected void onDisconnect() {
		Commands.pm.shutdown(); // Shutdown plugin manager
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
	
	public void logException(Exception e, String target) {
		this.logException(e);
		
		if (e.getMessage() == null) {
			
			this.notice(target, e.getClass().getName()); 
			this.notice(target, "Please report to the botmaster.");
		}
		else {
			this.notice(target, e.getMessage() + " - Please report to the botmaster.");
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

	public static String getBotName() {
		return JavaBot.NAME;
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

	public static long getAuthenciationDelay() {
		return JavaBot.AUTHENCIATION_DELAY;
	}
	
	public static int getPortNumber() {
		return JavaBot.PORT;
	}
	
	public static boolean ifSslConnection() {
		return JavaBot.SSL;
	}

	public static boolean getProtectMode() {
		return PROTECT_MODE;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
