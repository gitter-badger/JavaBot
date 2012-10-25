package javaBot.plugins.intl;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import javaBot.Commands;
import javaBot.JavaBot;

public class pluginHelp {
	private static final String DEBUG = "[DEBUG]";
	
    public static ArrayList<String> references = new ArrayList<String>();
    public static ArrayList<String> texts      = new ArrayList<String>();
    public static ArrayList<String> syntax     = new ArrayList<String>();

    public static void addEntry(String reference, String syntaxString, String text) {
    	if (!pluginHelp.references.contains(reference)) { // check if it doesn't contain
    		pluginHelp.references.add(reference);
    		pluginHelp.texts.add(text);
    		pluginHelp.syntax.add(syntaxString);
    	}
    }
    
    public static void addDebugEntry(String reference, String text) {
    	if (!pluginHelp.references.contains(DEBUG + reference)) { // check if it doesn't contain
    		pluginHelp.references.add(DEBUG + reference);
    		pluginHelp.texts.add(text);
    		pluginHelp.syntax.add("Type bot's nick + " + reference);
    	}
    }
    
    public static void msgReference(JavaBot bot, String nick) {
        int          counter = 0;
        StringBuffer string  = new StringBuffer("");

        for (int i = 0; i < references.size(); i++) {
            string.append(references.get(i));
            string.append(" ");
            counter++;

            if (counter == 5) {    // once it reaches 5 "references"
                bot.notice(nick, string.toString());
                string  = new StringBuffer("");
                counter = 0;       // reset
            }
        }
    }
    
    public static void msgSpecific(JavaBot bot, String nick, String command) {
        if (references.contains(command)) {    // if the command is inside references
            int index = references.indexOf(command);
            bot.notice(nick, command + " | " + syntax.get(index) + " | " + texts.get(index));
        } else if (references.contains(DEBUG + command)) {
            int index = references.indexOf(DEBUG + command);
            bot.notice(nick, DEBUG + command + " | " + syntax.get(index) + " | " + texts.get(index));
        }
        else {
            bot.notice(nick, "The command \"" + command + "\" cannot be found.");
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
