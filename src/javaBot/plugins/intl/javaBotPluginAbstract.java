package javaBot.plugins.intl;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
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
    
    /** Helper methods to make writing plugins extraordinarily easy! **/
    
    public final boolean matchesReference(String reference) {
    	return message.trim().equals(JavaBot.getPrefix() + reference);
    }
    
    public final boolean matchesStartReference(String reference) {
    	return message.trim().startsWith(JavaBot.getPrefix() + reference + " ");
    }
    
    public final boolean matches(String string) {
    	return message.startsWith(string);
    }
    
    public final boolean isAuthenciated(String nick) {
    	return JavaBot.isAuthenciated(nick);
    }
    
    public final boolean isSenderAuthenciated() {
    	return JavaBot.isAuthenciated(sender);
    }
    
    public final void notEnoughStatus() {
    	Commands.notEnoughStatus(sender);
    }
    
    public final void notEnoughStatus(String nick) {
    	Commands.notEnoughStatus(nick);
    }
    
    public final String checkParameter(int position) {
    	return Commands.checkParameter(message)[position];
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
