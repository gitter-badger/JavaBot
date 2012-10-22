package javaBot;

//~--- non-JDK imports --------------------------------------------------------

import java.util.ArrayList;
import javaBot.tools.DatabaseReader;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class Authenciation {
    static JavaBot                bot;
    static String                 message;
    static String                 sender;
    final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
    
    public void updateVariables(JavaBot bot, String sender, String message) {
        Authenciation.bot     = bot;
        Authenciation.sender  = sender;
        Authenciation.message = message;
    }

    /**
     * Method to check if any users exist - if not let anyone register
     * themselves without authenciation This method must be called to check for
     * any user that exists in the database for every command that requires
     * authenciation.
     */
    public static boolean checkNoUsers() {
        final DatabaseReader    dbreader = new DatabaseReader("users");
        final ArrayList<String> users    = new ArrayList<String>();

        try {
            dbreader.setCom(dbreader.getCon().createStatement());
            dbreader.setRec(dbreader.getCom().executeQuery("select * from \"users\""));

            while (dbreader.getRec().next()) {
                users.add(dbreader.getRec().getString("username"));
            }
        } catch (final Exception e) {
            bot.logException(e);
        }

        if (users.size() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void run() {
        if (Authenciation.message.startsWith(JavaBot.getPrefix() + "login ")) {
            String                  user      = Commands.checkParameter(Authenciation.message)[0];
            String                  password  = Commands.checkParameter(Authenciation.message)[1];
            final ArrayList<String> users     = new ArrayList<String>();
            final ArrayList<String> passwords = new ArrayList<String>();
            final DatabaseReader    dbreader  = new DatabaseReader("users");

            try {
                dbreader.setCom(dbreader.getCon().createStatement());
                dbreader.setRec(dbreader.getCom().executeQuery("select * from \"users\""));

                while (dbreader.getRec().next()) {
                    users.add(dbreader.getRec().getString("username"));
                    passwords.add(dbreader.getRec().getString("password"));
                }

                if (users.contains(user)) {
                    final int index = users.indexOf(user);

                    if (passwordEncryptor.checkPassword(password, passwords.get(index))) {
                        JavaBot.addToAuthenciated(Authenciation.sender);
                        Authenciation.bot.notice(Authenciation.sender, "You have been authenciated.");
                    } else {
                        try {
                            Thread.sleep(JavaBot.getAuthenciationDelay());
                        } catch (final Exception e) {
                            bot.log(e.getStackTrace().toString());
                        }

                        Authenciation.bot.notice(Authenciation.sender, "Password and username pair does not match.");
                    }
                } else {
                    try {
                        Thread.sleep(JavaBot.getAuthenciationDelay());
                    } catch (final Exception e) {
                        bot.log(e.getStackTrace().toString());
                    }

                    Authenciation.bot.notice(Authenciation.sender, "Password and username pair does not match.");
                }

                user     = "";
                password = "";

                for (int i = 0; i < users.size(); i++) {
                    users.remove(i);
                    passwords.remove(i);
                }
            } catch (final NullPointerException e1) {
                bot.log("users.odb cannot be accessed.");
                bot.logException(e1, sender);
            } catch (final Exception e2) {
                bot.logException(e2, sender);
            }
        } else if (Authenciation.message.equalsIgnoreCase(JavaBot.getPrefix() + "logout")) {
            if (JavaBot.isInAuthenciated(Authenciation.sender)) {
                JavaBot.removeFromAuthenciated(Authenciation.sender);
                Authenciation.bot.notice(Authenciation.sender, "You are now logged out.");
            } else {
                Commands.notEnoughStatus(Authenciation.sender);
            }
        } else if (Authenciation.message.startsWith(JavaBot.getPrefix() + "user_add ")) {
            if (JavaBot.isAuthenciated(Authenciation.sender)) {
                String user     = Commands.checkParameter(Authenciation.message)[0];
                String password = Commands.checkParameter(Authenciation.message)[1];

                Authenciation.bot.notice(Authenciation.sender,
                                         "Add these details to the database: ID " + user + " "
                                         + passwordEncryptor.encryptPassword(password));
                user     = "";
                password = "";
            } else {
                Commands.notEnoughStatus(Authenciation.sender);
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
