package javaBot;

public class Security {
    JavaBot bot;
    String  botName;
    String  channel;
    String  hostname;
    String  message;
    String  sender;
    
    public void updateVariables(JavaBot bot, String channel, String sender, String message, String hostname) {
        this.bot      = bot;
        this.channel  = channel;
        this.sender   = sender;
        this.message  = message;
        this.hostname = hostname;
        this.botName  = JavaBot.getBotName(bot);
    }

    public void run() {
        if (this.message.startsWith("!kick") && this.message.endsWith(this.botName)) {
            this.bot.joinChannel(this.channel);

            if (JavaBot.getProtectMode()) {
                StringBuffer kicker = new StringBuffer(this.hostname);

                kicker.insert(0, "!*@*");
                this.bot.ban(this.channel, kicker.toString());
                this.bot.kick(this.channel, this.sender);
            }
        } else if (this.message.startsWith("!ban") && this.message.endsWith(this.botName)) {
            this.bot.sendMessage("ChanServ", "unban " + this.botName + "!*@*");
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


//~ Formatted by Jindent --- http://www.jindent.com
