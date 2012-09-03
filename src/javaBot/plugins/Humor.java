package javaBot.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import wei2912.utilities.Generator;

@PluginImplementation
/** A plugin that lets you generate humor **/
public class Humor extends javaBotPluginAbstract {

	static private JavaBot	 bot;
	static private String	 message;
	static private String	 sender;

	static ArrayList<String>	humorArray	= new ArrayList<String>();
	File	                 humor	       = new File("files/humor.txt");
	BufferedReader	         in;
	String	                 line;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {
		Humor.bot = bot;
		Humor.message = message;
		Humor.sender = sender;

		/** Humor */
		try {
			this.in = new BufferedReader(new FileReader(this.humor));
			this.line = this.in.readLine();

			while (this.line != null) {
				Humor.humorArray.add(this.line);
				this.line = this.in.readLine();
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (Humor.message.equalsIgnoreCase(JavaBot.getPrefix() + "humor")) {
			final int a = Generator.generateInt(0, Humor.humorArray.size() - 1);
			Humor.bot.notice(Humor.sender, Humor.humorArray.get(a));
		}
	}
}