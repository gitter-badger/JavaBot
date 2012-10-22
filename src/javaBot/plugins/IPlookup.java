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
    public void onStart() {
        pluginHelp.addEntry("iplookup", "iplookup [host]", "Looks up the IPs of the servers.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.message = message;
        this.channel = channel;
        this.sender  = sender;
    }

    @Override
    public void run() {
        if (matchesStartReference("iplookup")) {
            final String server = Commands.checkParameter(this.message)[0];

            try {
                final InetAddress[] addresses = InetAddress.getAllByName(server);
                final StringBuffer  servers   = new StringBuffer("");

                for (final InetAddress ip : addresses) {
                    servers.append(ip.toString());
                    servers.append(" | ");
                }

                this.bot.notice(this.sender, servers.toString());
            } catch (final UnknownHostException e) {
            	this.bot.notice(this.sender, "Unknown host.");
            }
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com