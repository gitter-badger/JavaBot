package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import javaBot.tools.htmlParser;

import net.xeoh.plugins.base.annotations.PluginImplementation;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.util.ArrayList;

@PluginImplementation

/** A plugin that checks for games going on in Warzone2100 */
public class wz2100GamesChecker extends javaBotPluginAbstract {
    static private JavaBot bot;
    static private String  message;
    static private String  sender;

    public void onStart() {
        pluginHelp.addEntry("wz2100", "wz2100", "Check Warzone2100 games that are currently hosted.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        wz2100GamesChecker.bot     = bot;
        wz2100GamesChecker.message = message;
        wz2100GamesChecker.sender  = sender;
    }

    @Override
    public void run() {
        if (wz2100GamesChecker.message.equalsIgnoreCase(JavaBot.getPrefix() + "wz2100")) {
            final String[] array = htmlParser.readWebsite("http://wz2100.net/old-site/lobbyserver");
            int            line  = 0;

            for (; line < array.length; line++) {    // Makes a loop to find the

                // first unordered list after
                // warzone-content
                if (array[line].equalsIgnoreCase("<div class=\"warzone-content\">")) {
                    break;
                }
            }

            for (; line < array.length; line++) {    // Makes a loop to find the

                // first unordered list after
                // warzone-content
                if (array[line].equalsIgnoreCase("<ul>")) {
                    break;
                }
            }

            // Obtain content from every list
            final ArrayList<String> games = new ArrayList<String>();

            for (; line < array.length; line++) {                              // Makes a loop to find the

                // lists
                if (array[line].equalsIgnoreCase("<li>")) {
                    for (; line < array.length; line++) {                      // Makes a loop to

                        // find the content of
                        // the lists
                        if (array[line].contains("<strong>")) {
                            String gameName = array[line].replaceAll(".*<strong>", "");

                            gameName = gameName.replaceAll("</strong></a>", "");
                            System.out.println(gameName);
                            games.add(gameName);
                        } else if (array[line].equalsIgnoreCase("</ul>")) {    // Breaks

                            // at
                            // the
                            // end
                            // of
                            // the
                            // unordered
                            // list
                            break;
                        }
                    }

                    break;
                }
            }

            // Now, convert values of arrayList to a string.
            final StringBuffer buffer = new StringBuffer("");

            if (games.size() == 0) {
                buffer.append("There are no games being hosted.");
            } else {
                for (int i = 0; i < games.size(); i++) {
                    buffer.append(games.get(i));
                    buffer.append(" | ");
                }
            }

            wz2100GamesChecker.bot.notice(wz2100GamesChecker.sender, buffer.toString());
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
