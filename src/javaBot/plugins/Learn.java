package javaBot.plugins;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import wei2912.utilities.Generator;

@PluginImplementation
/** Obtains rss feeds from the LinuxTuts blog, displays tutorials **/
public class Learn extends javaBotPluginAbstract {
	static URL	    url;
	static JavaBot	bot;
	static String	channelString;
	static String	sender;
	static String	message;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender){
		Learn.bot = bot;
		Learn.channelString = channel;
		Learn.sender = sender;
		Learn.message = message;
		
		try {
			Learn.url = new URL("http://feeds.feedburner.com/sytes/rBaL?format=xml");
		} catch (MalformedURLException e) {
			bot.logException(e);
		}
	}

	@Override
	public void run() {
		if (Learn.message.equalsIgnoreCase(JavaBot.getPrefix() + "learn")) {
			this.generate(".*");
		}
	}

	public void generate(String query) {
		final ArrayList<String> array = new ArrayList<String>();
		Document doc = null;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(url.openStream());
		}
		catch (Exception e) {
			bot.logException(e);
		}
		
		 for (javaxt.rss.Feed feed : new javaxt.rss.Parser(doc).getFeeds()) {
			 //TODO add parser here which will add to array.
		 }

		if (array.size() == 0) {
			Learn.bot.notice(Learn.sender, "No RSS item can be found.");
		}
		else {
			Learn.bot.notice(Learn.sender, array.get(Generator.generateInt(0, array.size() - 1)));
		}
	}

	public static boolean isMatching(String title, String description, String regex) {
		title = title.toLowerCase();
		regex = regex.toLowerCase();
		description = description.toLowerCase();

		if (title.matches(".*" + regex + ".*")
		        || description.contains(".*" + regex + ".*")) { return true; }

		return false;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
