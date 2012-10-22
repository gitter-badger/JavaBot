package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.net.InetAddress;
import java.net.UnknownHostException;

@PluginImplementation

/** Looks up IP addresses */
public class IPlookup extends javaBotPluginAbstract {
    static JavaBot bot;
    static String  channel;
    static String  message;
    static String  sender;

    public void onStart() {
        pluginHelp.addEntry("iplookup", "iplookup [host]", "Looks up the IPs of the servers.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        IPlookup.bot     = bot;
        IPlookup.message = message;
        IPlookup.channel = channel;
        IPlookup.sender  = sender;
    }

    @Override
    public void run() {
        if (matchesStartReference("iplookup")) {
            final String server = Commands.checkParameter(IPlookup.message)[0];

            try {
                final InetAddress[] addresses = InetAddress.getAllByName(server);
                final StringBuffer  servers   = new StringBuffer("");

                for (final InetAddress ip : addresses) {
                    servers.append(ip.toString());
                    servers.append(" | ");
                }

                IPlookup.bot.notice(IPlookup.sender, servers.toString());
            } catch (final UnknownHostException e) {
                IPlookup.bot.notice(IPlookup.sender, "Unknown host.");
            }
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com