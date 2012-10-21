package javaBot.plugins.intl;

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class pluginHelp {
    public static Set<String> references = new HashSet<String>();
    public static Set<String> texts      = new HashSet<String>();
    public static Set<String> syntax     = new HashSet<String>();

    public static void addEntry(String reference, String syntaxString, String text) {
        pluginHelp.references.add(reference);
        pluginHelp.texts.add(text);
        pluginHelp.syntax.add(syntaxString);
    }

    public static String[] getReferences() {
        return sort(references);
    }

    public static String[] getTexts() {
        return sort(texts);
    }

    public static String[] getSyntax() {
        return sort(syntax);
    }

    private static String[] sort(Set<String> set) {
        String[] array = set.toArray(new String[0]);

        Arrays.sort(array);

        return array;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
