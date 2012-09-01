package javaBot.plugins.intl;

import javaBot.JavaBot;

public abstract class javaBotPluginAbstract implements javaBotPlugin {
	String	help;
	String	identifier;

	/** To be overridded **/
	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {

	}

	public void run() {

	}
}
