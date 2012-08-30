package javaBot.plugins;

// ~--- non-JDK imports --------------------------------------------------------

import java.io.IOException;
import java.util.ArrayList;

import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.commons.validator.routines.UrlValidator;

import wei2912.utilities.Generator;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.impl.basic.Item;
import de.nava.informa.parsers.FeedParser;

@PluginImplementation
/** Obtains rss feeds from the LinuxTuts blog, displays tutorials **/
public class Learn extends javaBotPluginAbstract {
	static String	url	= "http://feeds.feedburner.com/sytes/rBaL?format=xml";
	static JavaBot	bot;
	static String	channelString;
	static String	sender;
	static String	message;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {
		Learn.bot = bot;
		Learn.channelString = channel;
		Learn.sender = sender;
		Learn.message = message;
	}

	@Override
	public void run() {
		if (Learn.message.equalsIgnoreCase(JavaBot.getPrefix() + "learn")) {
			this.generate(".*");
		}
	}

	public void generate(String query) {
		Item item = null;
		final ArrayList<String> array = new ArrayList<String>();

		ChannelIF channel = null;
		try {
			channel = FeedParser.parse(new ChannelBuilder(), Learn.url);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		catch (final ParseException e) {
			e.printStackTrace();
		}

		if (channel.getItems().size() == 0) {
			Learn.bot.notice(Learn.sender,
			        "Link is invalid, please report to the botmaster. The link is: "
			                + Learn.url);
		}
		else {
			for (int i = 0; i < channel.getItems().size(); i++) {
				item = (Item) channel.getItems().toArray()[i];

				final UrlValidator urlValidator = new UrlValidator();

				if (RSSfilter.isMatching(item, query)) {
					if (!array.contains(item.getTitle() + " (" + item.getLink()
					        + ")")
					        && urlValidator.isValid(item.getLink().toString())) { // Checks
						                                                          // if
						                                                          // link
						                                                          // is
						                                                          // valid
						array.add(item.getTitle() + " (" + item.getLink() + ")");
					}
				}
			}
		}

		if (array.size() == 0) {
			Learn.bot.notice(Learn.sender, "No RSS item can be found.");
		}
		else {
			Learn.bot.notice(Learn.sender,
			        array.get(Generator.generateInt(0, array.size() - 1)));
		}
	}
}

class RSSfilter {
	public static boolean isMatching(Item item, String regex) {
		String title = item.getTitle();
		String description = item.getDescription();

		title = title.toLowerCase();
		regex = regex.toLowerCase();
		description = description.toLowerCase();

		if (title.matches(".*" + regex + ".*")
		        || description.contains(".*" + regex + ".*")) { return true; }

		return false;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
