package javaBot.plugins.intl;

import javaBot.JavaBot;

import net.xeoh.plugins.base.Plugin;

public interface javaBotPlugin extends Plugin, Runnable {
	public void init(JavaBot bot, String message, String channel, String sender);

	public void exec();
}
