package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.w3c.dom.Document;

import wei2912.utilities.Generator;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@PluginImplementation

/** Obtains rss feeds from the LinuxTuts blog, displays tutorials */
public class Learn extends javaBotPluginAbstract {
    static JavaBot bot;
    static String  channelString;
    static String  message;
    static String  sender;
    static URL     url;

    public void onStart() {
        pluginHelp.addEntry("learn", "learn", "Learn something new! By Linux Tuts.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        Learn.bot           = bot;
        Learn.channelString = channel;
        Learn.sender        = sender;
        Learn.message       = message;

        try {
            Learn.url = new URL("http://feeds.feedburner.com/newlinuxtuts?format=xml");
        } catch (MalformedURLException e) {
            bot.logException(e);
        }
    }

    @Override
    public void run() {
        if (Learn.message.equalsIgnoreCase(JavaBot.getPrefix() + "learn")) {
            this.generate();
        }
    }

    public void generate() {
        Document          doc   = null;
        ArrayList<String> array = new ArrayList<String>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder        db  = dbf.newDocumentBuilder();

            doc = db.parse(url.openStream());
        } catch (Exception e) {
            bot.logException(e);
        }

        for (javaxt.rss.Feed feed : new javaxt.rss.Parser(doc).getFeeds()) {
            for (javaxt.rss.Item item : feed.getItems()) {
                StringBuffer string = new StringBuffer("");

                string.append("[" + item.getTitle() + "]");
                string.append(" " + item.getLink().toString());
                array.add(string.toString());
            }
        }

        if (array.size() == 0) {
            Learn.bot.notice(Learn.sender, "No RSS item can be found.");
        } else {
            int random = new Generator().nextInt(0, array.size() - 1);

            Learn.bot.notice(Learn.sender, array.get(random));
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
