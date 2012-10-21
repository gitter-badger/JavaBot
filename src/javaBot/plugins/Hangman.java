package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import javaBot.tools.DatabaseReader;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import wei2912.utilities.Generator;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginImplementation

/** Hangman game! */
public class Hangman extends javaBotPluginAbstract {
    static String               category   = "";
    static DatabaseReader       dbreader   = new DatabaseReader("hangman");
    static int                  difficulty = 3;
    static boolean              hangman    = false;
    static ArrayList<String>    beforeWord = new ArrayList<String>();    // Prevents repeat of last word
    static String               hidden     = "";
    static int                  lives      = 0;
    static String               word       = "";
    static ArrayList<Character> chosen     = new ArrayList<Character>();
    static JavaBot              bot;
    static String               channel;
    static String               message;
    static String               sender;

    public void onStart() {
        pluginHelp.addEntry("hm", "hm", "Starts a game of hangman.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        Hangman.bot     = bot;
        Hangman.channel = channel;
        Hangman.sender  = sender;
        Hangman.message = message;
    }

    @Override
    public void run() {
        if (Hangman.message.equalsIgnoreCase(JavaBot.getPrefix() + "hm")) {
            Hangman.bot.sendMessage(Hangman.channel, Hangman.sender + " has created a game of hangman.");
            Hangman.bot.sendMessage(
                Hangman.channel,
                "Guess a single letter. If you manage to guess a letter in the word, all instances of that letter will be shown. If you don't, you lose a life.");
            Hangman.bot.sendMessage(
                Hangman.channel,
                "Everyone can play in this game! Just type " + JavaBot.getPrefix()
                + "hm <Letter> to play. For example, you can type " + JavaBot.getPrefix()
                + "hm a to show all instances of a, if any. You can also type the full word if you already know it, but only do so if certain.");
            Hangman.hangman = true;
            Hangman.selectWord();
        } else if (Hangman.message.startsWith(JavaBot.getPrefix() + "hm ")) {
            Hangman.play(Hangman.message);
        }
    }

    public static void play(String message) {
        if (Hangman.hangman == true) {
            String                  letter  = Commands.checkParameter(message)[0];
            Pattern                 pattern = null;
            java.util.regex.Matcher matcher = null;

            try {
                pattern = Pattern.compile("[a-zA-Z]");
                matcher = pattern.matcher(letter);
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (matcher.matches()) {
                letter = letter.toLowerCase();                    // Set to lower case

                if (Hangman.chosen.contains(letter.charAt(0))) {
                    Hangman.bot.sendMessage(Hangman.channel, "Letter [" + letter + "] already chosen.");
                } else if (Hangman.word.contains(letter)) {       // If equal
                    Hangman.chosen.add(letter.charAt(0));

                    final StringBuffer hiddenBuffer = new StringBuffer(Hangman.hidden);

                    for (int i = 0; i < Hangman.word.length(); i++) {
                        if (Hangman.word.charAt(i) == letter.charAt(0)) {
                            hiddenBuffer.setCharAt(i, letter.charAt(0));
                        }
                    }

                    Hangman.hidden = hiddenBuffer.toString();

                    if (Hangman.hidden.contains(".")) {
                        Hangman.bot.sendMessage(Hangman.channel, "Word is now [" + Hangman.hidden + "].");
                    } else {
                        Hangman.bot.sendMessage(Hangman.channel, "Word is now [" + Hangman.hidden + "]. You've won!");

                        if (Hangman.beforeWord.size() >= 50) {    // Reset
                            Hangman.beforeWord.remove(0);         // Removes first entry
                        }

                        Hangman.beforeWord.add(Hangman.word);     // Prevents

                        // duplicate word

                        if (!((Hangman.difficulty >= 8) || (Hangman.difficulty <= 3))) {
                            Hangman.difficulty++;                 // Adds to difficulty
                        }

                        try {
                            Hangman.hangman = false;
                            Thread.sleep(5000);
                            Hangman.hangman = true;
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                        Hangman.selectWord();
                    }
                } else {                                          // If not equal
                    Hangman.lives--;
                    Hangman.chosen.add(letter.charAt(0));

                    if (Hangman.lives <= 0) {
                        Hangman.bot.sendMessage(Hangman.channel,
                                                "Nope, wrong letter. You've lost! The word was [" + Hangman.word
                                                + "].");

                        if (Hangman.beforeWord.size() >= 50) {    // Reset
                            Hangman.beforeWord.remove(0);         // Removes first entry
                        }

                        Hangman.beforeWord.add(Hangman.word);     // Prevents

                        // duplicate word

                        if (!((Hangman.difficulty >= 6) || (Hangman.difficulty <= 1))) {
                            Hangman.difficulty--;                 // Adds to difficulty
                        }

                        try {
                            Hangman.hangman = false;
                            Thread.sleep(5000);
                            Hangman.hangman = true;
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                        Hangman.selectWord();
                    } else {
                        Hangman.bot.sendMessage(Hangman.channel,
                                                "Nope, wrong letter. You now have " + Hangman.lives + " lives.");
                    }
                }
            } else if (letter.toLowerCase().equals(Hangman.word)) {
                Hangman.bot.sendMessage(Hangman.channel, "You've won!");

                if (Hangman.beforeWord.size() >= 50) {    // Reset
                    Hangman.beforeWord.remove(0);         // Removes first entry
                }

                Hangman.beforeWord.add(Hangman.word);     // Prevents duplicate word

                if (!((Hangman.difficulty >= 6) || (Hangman.difficulty <= 1))) {
                    Hangman.difficulty++;                 // Adds to difficulty
                }

                try {
                    Hangman.hangman = false;
                    Thread.sleep(5000);
                    Hangman.hangman = true;
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                Hangman.selectWord();
            } else if (letter.length() > 1) {             // If guessed word failed
                Hangman.lives--;

                if (Hangman.lives <= 0) {
                    Hangman.bot.sendMessage(Hangman.channel,
                                            "Nope, wrong word. You've lost! The word was [" + Hangman.word + "].");

                    if (Hangman.beforeWord.size() >= 50) {    // Reset
                        Hangman.beforeWord.remove(0);         // Removes first entry
                    }

                    Hangman.beforeWord.add(Hangman.word);     // Prevents duplicate

                    // word

                    if (!((Hangman.difficulty >= 6) || (Hangman.difficulty <= 1))) {
                        Hangman.difficulty--;                 // Adds to difficulty
                    }

                    try {
                        Hangman.hangman = false;
                        Thread.sleep(5000);
                        Hangman.hangman = true;
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                    Hangman.selectWord();
                } else {
                    Hangman.bot.sendMessage(Hangman.channel,
                                            "Nope, wrong word. You now have " + Hangman.lives + " lives.");
                }
            } else {
                Hangman.bot.sendMessage(Hangman.channel, "Invalid value.");
            }
        } else {
            Hangman.bot.sendMessage(Hangman.channel,
                                    "Please type " + JavaBot.getPrefix()
                                    + "hm without any parameters to start a new game.");
        }
    }

    public static void selectWord() {
        Hangman.chosen = new ArrayList<Character>();

        final ArrayList<String> words = new ArrayList<String>();

        try {
            Hangman.dbreader.setCom(Hangman.dbreader.getCon().createStatement());
            Hangman.dbreader.setRec(Hangman.dbreader.getCom().executeQuery("select * from \"hangman\""));

            while (Hangman.dbreader.getRec().next()) {
                words.add(Hangman.dbreader.getRec().getString("word"));
            }

            int random = new Generator().nextInt(0, words.size() - 1);    // To be

            // used
            // after
            // the
            // number
            // of lives
            // are
            // found

            // Lives
            final ArrayList<String> lives = new ArrayList<String>();

            Hangman.dbreader.setCom(Hangman.dbreader.getCon().createStatement());
            Hangman.dbreader.setRec(Hangman.dbreader.getCom().executeQuery("select * from \"hangman\""));

            while (Hangman.dbreader.getRec().next()) {
                lives.add(Hangman.dbreader.getRec().getString("lives"));
            }

            // Adaptive difficulty
            do {
                random        = new Generator().nextInt(0, words.size() - 1);
                Hangman.word  = words.get(random).toLowerCase();
                Hangman.lives = Integer.parseInt(lives.get(random));
            } while (Hangman.beforeWord.contains(Hangman.word) || (Hangman.lives < Hangman.difficulty));    // Gets a new word

            // never used.

            // Convert to hidden
            final StringBuffer hiddenBuffer = new StringBuffer("");
            final Pattern      pattern      = Pattern.compile("[A-Za-z]");

            for (int i = 0; i < Hangman.word.length(); i++) {
                final Matcher matcher = pattern.matcher(String.valueOf(Hangman.word.charAt(i)));

                // Append all letters as . and punctuation as their original
                // form.
                if (matcher.matches()) {
                    hiddenBuffer.append(".");
                } else {
                    hiddenBuffer.append(Hangman.word.charAt(i));
                }
            }

            Hangman.hidden = hiddenBuffer.toString();

            // Category
            String                  category   = "";
            final ArrayList<String> categories = new ArrayList<String>();

            Hangman.dbreader.setCom(Hangman.dbreader.getCon().createStatement());
            Hangman.dbreader.setRec(Hangman.dbreader.getCom().executeQuery("select * from \"hangman\""));

            while (Hangman.dbreader.getRec().next()) {
                categories.add(Hangman.dbreader.getRec().getString("category"));
            }

            category = categories.get(random);
            Hangman.bot.sendMessage(Hangman.channel,
                                    "New word is [" + Hangman.hidden + "] in category [" + category + "]. You've "
                                    + Hangman.lives + " lives.");
            System.out.println(Hangman.difficulty);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
