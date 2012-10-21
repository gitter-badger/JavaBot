package javaBot.plugins.intl;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

import net.xeoh.plugins.base.Plugin;

public interface javaBotPlugin extends Plugin {
    public void init(JavaBot bot, String message, String channel, String sender);

    public void run();

    public void onStart();
}


//~ Formatted by Jindent --- http://www.jindent.com
