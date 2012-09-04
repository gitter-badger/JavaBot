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
		if (this.message.startsWith("!kick") && this.message.endsWith(JavaBot.getBotName())) {
			this.bot.joinChannel(this.channel);

			if (JavaBot.getProtectMode()) {
				StringBuffer kicker = new StringBuffer(this.hostname);

				kicker.insert(0, "!*@*");
				this.bot.ban(this.channel, kicker.toString());
				this.bot.kick(this.channel, this.sender);
			}
		}
		else if (this.message.startsWith("!ban")
		        && this.message.endsWith(JavaBot.getBotName())) {

			this.bot.sendMessage("ChanServ", "unban " + JavaBot.getBotName() + "!*@*");
			this.bot.joinChannel(this.channel);

			if (JavaBot.getProtectMode()) {
				StringBuffer kicker = new StringBuffer(this.hostname);

				kicker.insert(0, "!*@*");
				this.bot.ban(this.channel, kicker.toString());
				this.bot.kick(this.channel, this.sender);
			}
		}
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
