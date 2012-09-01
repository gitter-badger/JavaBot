package javaBot;

public class Security {
	JavaBot	bot;
	String	channel;
	String	hostname;
	String	message;
	String	sender;

	public Security(JavaBot bot, String channel, String sender, String message,
	        String hostname) {
		this.bot = bot;
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.hostname = hostname;
	}

	public void run() {

		/** Security - checks messages sent */
		if (this.message.startsWith("!kick") && this.message.endsWith(JavaBot.getBotName())) {

			// If get kicked by chanserv from another user
			this.bot.joinChannel(this.channel);

			// For a better ban
			final StringBuffer kicker = new StringBuffer(this.hostname);

			kicker.insert(0, "!*@*");
			this.bot.ban(this.channel, kicker.toString());
			this.bot.kick(this.channel, this.sender);
		}
		else if (this.message.startsWith("!ban")
		        && this.message.endsWith(JavaBot.getBotName())) {

			// If get kicked by chanserv from another user
			this.bot.sendMessage("ChanServ", "recover " + this.channel);
			this.bot.joinChannel(this.channel);

			// For a better ban
			final StringBuffer kicker = new StringBuffer(this.hostname);

			kicker.insert(0, "!*@*");
			this.bot.ban(this.channel, kicker.toString());
			this.bot.kick(this.channel, this.sender);

			try {
				Thread.sleep(1000);
			}
			catch (final InterruptedException e) {
				bot.log(e.getStackTrace().toString());
			}

			this.bot.setMode(this.channel, "-il");
		}
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
