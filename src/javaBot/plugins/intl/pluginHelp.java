package javaBot.plugins.intl;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class pluginHelp {
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
    	if (!pluginHelp.references.contains(reference)) { // check if it doesn't contain
    		pluginHelp.references.add(reference);
    		pluginHelp.texts.add("DEBUG: " + text);
    		pluginHelp.syntax.add("Add bot's nick + " + reference + " EXAMPLE: \"JavaBot, " + reference + "\"");
    	}
    }

    public static String[] getReferences() {
        return getArray(references);
    }

    public static String[] getTexts() {
        return getArray(texts);
    }

    public static String[] getSyntax() {
        return getArray(syntax);
    }
    
    private static String[] getArray(ArrayList<String> arraylist) {
    	return (String[]) arraylist.toArray(new String[0]);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
