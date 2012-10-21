package javaBot;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.plugins.intl.pluginHelp;

public class onStart {

    // classes for commands to be executed on start.
    public onStart() {
        pluginHelp.addEntry("login", "login [user] [password]",
                            "Adds you to the authenciated list and mark you as authenciated.");
        pluginHelp.addEntry("logout", "logout", "Logout for security reasons.");
        pluginHelp.addEntry("user_add", "user_add [user] [password]", "Adds a user to the database.");
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
