package javaBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import javaBot.plugins.intl.javaBotPlugin;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import wei2912.utilities.Generator;

/**
 * @version 1.5.0
 * @author wei2912
 */

/** JavaBot main class */
public class JavaBot extends PircBot implements Runnable {
	// STATIC "MAGIC" VARIABLES \\
	private final static String SERVERS_SEPERATOR = "&";
	private final static String CHANNELS_SEPERATOR = ",";
	// STATIC "MAGIC" VARIABLES \\
	
    // CONFIG VARIABLES \\
	private static long   AUTHENCIATION_DELAY = 0;
    private static String CHANNELS            = "";
    private static long   DELAY               = 0;
    private static String            NAMES              = "";
    private static String            NICKSERV_PASSWORDS = "";
    private static String            PREFIX             = "";
    private static String            SERVERS            = "";
    private static boolean           PROTECT_MODE       = false;
    private static boolean           PRIV_MSG_LOGGING   = false;
    private static ArrayList<String> AUTHENCIATED       = new ArrayList<String>();    // authenciated user list
    // CONFIG VARIABLES \\
    
    static ArrayList<String>         configArray        = new ArrayList<String>();
    static ArrayList<String>         configNameArray    = new ArrayList<String>();
    private static BufferedReader    FileReader;
    private static Collection<javaBotPlugin> plugins;
    
    // INFORMATION VARIABLES \\
    private static String[] SERVERS_ARRAY;    // list of servers
    // INFORMATION VARIABLES \\

    // INSTANCE VARIABLES \\
    private String   name              = "";
    private String   nickserv_password = "";
    private String   server            = "";
    private String[] channels_array;    // list of channels
    // INSTANCE VARIABLES \\
    
    // SOFTWARE VARIABLES (???) \\
    private Commands commands = new Commands();
    private Security security = new Security();
    private Authenciation authenciation = new Authenciation();
    // SOFTWARE VARIABLES (???) \\
    
    // to read the server list.
    public static void main(String args[]) {
        File config = new File("files/config.txt");

        try {
            JavaBot.FileReader = new BufferedReader(new FileReader(config));
            String line = null;
            line = JavaBot.FileReader.readLine();

            while (line != null) {
                final String[] array = line.split("=");

                if (array.length == 2) {
                    JavaBot.configNameArray.add(array[0].replaceAll("=", "").trim());
                    JavaBot.configArray.add(array[1].trim());
                }

                line = JavaBot.FileReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaBot.getAllConfig();

        String[] channels  = JavaBot.CHANNELS.split(SERVERS_SEPERATOR);              
        String[] passwords = JavaBot.NICKSERV_PASSWORDS.split(SERVERS_SEPERATOR);  
        String[] names     = JavaBot.NAMES.split(SERVERS_SEPERATOR);

        /** TRIMMING VARIABLES */
        SERVERS_ARRAY = trimArray(SERVERS_ARRAY);
        channels = trimArray(channels);
        passwords = trimArray(passwords);
        names = trimArray(names);

        initPlugins();
        
        /** CLASSES onStart() */
        new onStart();

        /** PLUGINS onStart() */

        for (final javaBotPlugin plugin : plugins) {
            plugin.onStart();
        }

        for (int i = 0; i < SERVERS_ARRAY.length; i++) {
            JavaBot bot = new JavaBot();

            // setting of values respective to each instance of the bot
            bot.setServer(SERVERS_ARRAY[i]);
            bot.setBotName(names[i]);
            bot.setPassword(passwords[i]);
            bot.channels_array = channels[i].split(CHANNELS_SEPERATOR);    // split up channels
            new Thread(bot).start();
        }
    }

    private static void getAllConfig() {
        JavaBot.NAMES               = JavaBot.getConfig("nicks");
        JavaBot.SERVERS             = JavaBot.getConfig("servers");
        JavaBot.CHANNELS            = JavaBot.getConfig("channels");
        JavaBot.NICKSERV_PASSWORDS  = JavaBot.getConfig("nickserv_passwords");
        JavaBot.PREFIX              = JavaBot.getConfig("prefix");
        JavaBot.DELAY               = Long.parseLong(JavaBot.getConfig("messageDelay"));
        JavaBot.PROTECT_MODE        = Boolean.parseBoolean(JavaBot.getConfig("protectMode"));
        JavaBot.AUTHENCIATION_DELAY = Long.parseLong(JavaBot.getConfig("authenciationDelay"));
        JavaBot.PRIV_MSG_LOGGING    = Boolean.parseBoolean(JavaBot.getConfig("privMsgLog"));

        // channel array
        JavaBot.SERVERS_ARRAY = JavaBot.SERVERS.split(SERVERS_SEPERATOR);
    }

    public void run() {
        /** Bot starting up! */
        setName(name);
        setMessageDelay(JavaBot.DELAY);    // Set message delay.
        setVerbose(true);
        setPrivMsgVerbose(JavaBot.PRIV_MSG_LOGGING);

        try {
            this.connect(this.server);
        } catch (final NickAlreadyInUseException e) {
            name = name + new Generator().nextInt(1000, 9999);
            setName(name);

            try {
                this.connect(this.server);
            } catch (final Exception e2) {
                logException(e2);
            }
        } catch (final Exception e) {
            logException(e);
        }

        identify(nickserv_password);
        sendRawLine("MODE " + JavaBot.getBotName(this) + " +B");

        for (int i = 0; i < channels_array.length; i++) {
            joinChannel(channels_array[i]);
        }
    }

    /**
     * METHODS THAT DECIDE THE BEHAVIOUR OF EACH BOT
     */
    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        commands.updateVariables(this, sender, channel, message);
        security.updateVariables(this, channel, sender, message, hostname);
        
        commands.run();
        security.run();
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        commands.updateVariables(this, sender, sender, message);
        authenciation.updateVariables(this, sender, message);
        
        commands.run();
        authenciation.run();
    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
                          String recipientNick, String reason) {
        if (recipientNick.equals(name) &&!kickerNick.equals("ChanServ")) {
            this.joinChannel(channel);

            if (JavaBot.PROTECT_MODE) {
                final StringBuffer kicker = new StringBuffer(kickerHostname);

                kicker.insert(0, "!*@*");
                this.ban(channel, kicker.toString());
                this.kick(channel, kickerNick);
            }
        }
    }

    @Override
    protected void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname,
                                   String hostmask) {
        String newhostmask = hostmask.replace("*!*", ".*");

        newhostmask = newhostmask.replaceAll("[^\\.]*", "\\.*");

        if (("*!*@JavaBot".matches(newhostmask) || "*!*PircBotPPF@JavaBot".matches(newhostmask))
                &&!sourceNick.equals("ChanServ")) {
            if (JavaBot.PROTECT_MODE) {
                final StringBuffer kicker = new StringBuffer(sourceHostname);

                kicker.insert(0, "      ");
                this.ban(channel, kicker.toString());
                this.kick(channel, sourceNick);
            }

            this.unBan(channel, hostmask);
        }
    }

    @Override
    protected void onDisconnect() {
        Commands.pm.shutdown();    // Shutdown plugin manager
        System.exit(0);
    }
    
    protected void onJoin(String channel, String sender, String login, String hostname) {
        for (final javaBotPlugin plugin : plugins) {
            plugin.onJoin(channel, sender, login, hostname);
        }
    }
    
    protected void onPart(String channel, String sender, String login, String hostname) {
        for (final javaBotPlugin plugin : plugins) {
            plugin.onPart(channel, sender, login, hostname);
        }
    }

    /**
     * REFACTORED METHODS
     * LOOK UNDER HERE FOR DETAILS
     */
    
    private static void initPlugins() {
    	JSPFProperties props = new JSPFProperties();
        PluginManager pm = PluginManagerFactory.createPluginManager(props);
        pm.addPluginsFrom(new File("plugins/").toURI());
        JavaBot.plugins =  new PluginManagerUtil(pm).getPlugins(javaBotPlugin.class);
    }
    
    private static String[] trimArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }
    
    /**
     * HELPER METHODS
     */
    
    /**
     * Sends a notice if the target is in a channel the bot
     * is in. Or else, send a private message so that no
     * message will be lost.
     *
     * The reason for this implementation is that
     * the function NOTICE only works if the user
     * is in the same channel.
     *
     * @param sender Sender of message.
     * @param message Message to be sent.
     */
    public void notice(String sender, String message) {
        final String[] CHANNEls = JavaBot.CHANNELS.split(" ");
        boolean        found    = false;

        for (final String CHANNEl2 : CHANNEls) {
            final User[] userList = this.getUsers(CHANNEl2);

            for (final User user : userList) {
                if (user.getNick().equals(sender)) {
                    found = true;
                    break;
                }
            }

            if (found == true) { break; }
        }

        if (found == true) {
            this.sendNotice(sender, message);
        } else {
            this.sendMessage(sender, message);
        }
    }

    // bot logging
    public void logException(Exception e, String target) {
        this.logException(e);

        if (e.getMessage() == null) {
            this.notice(target, e.getClass().getName());
            this.notice(target, "Please report to the botmaster.");
        } else {
            this.notice(target, e.getMessage() + " - Please report to the botmaster.");
        }
    }

    public void setBotName(String config) {
        name = config;
    }

    public void setServer(String config) {
        server = config;
    }

    public void setPassword(String config) {
        nickserv_password = config;
    }

    private static String getConfig(String parameter) {
        if (JavaBot.configNameArray.contains(parameter)) {
            return JavaBot.configArray.get(JavaBot.configNameArray.indexOf(parameter));
        } else {
            throw new java.lang.NullPointerException(parameter + " not found in configNameArray. " + "\n"
                    + "Please recheck the config file.");
        }
    }

    public static void addToAuthenciated(String nick) {
        AUTHENCIATED.add(nick);
    }

    public static void removeFromAuthenciated(String nick) {
        if (JavaBot.isInAuthenciated(nick)) {
            AUTHENCIATED.remove(AUTHENCIATED.indexOf(nick));
        }
    }

    public static boolean isInAuthenciated(String nick) {
        return AUTHENCIATED.contains(nick);
    }

    public static boolean isAuthenciated(String nick) {
        return (JavaBot.isAuthenciated(nick) || Authenciation.checkNoUsers());
    }

    public static String getBotName(JavaBot bot) {
        return bot.name;
    }

    public static long getDelay() {
        return JavaBot.DELAY;
    }

    public static String getPrefix() {
        return JavaBot.PREFIX;
    }

    public static long getAuthenciationDelay() {
        return JavaBot.AUTHENCIATION_DELAY;
    }

    public static boolean getProtectMode() {
        return PROTECT_MODE;
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com