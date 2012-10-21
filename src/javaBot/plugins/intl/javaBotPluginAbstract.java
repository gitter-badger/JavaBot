package javaBot.plugins.intl;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

public abstract class javaBotPluginAbstract implements javaBotPlugin {
    protected JavaBot bot;
    protected String  channel;
    protected String  message;
    protected String  sender;

    /** To be overridded */
    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {}

    public void run() {}

    public void onStart() {}
}


//~ Formatted by Jindent --- http://www.jindent.com
