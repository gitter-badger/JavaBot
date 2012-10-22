package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import wei2912.utilities.Generator;

@PluginImplementation

/** A plugin that allows you to roll a dice and flip a coin */
public class Dice_Coin extends javaBotPluginAbstract {
	Generator generator = new Generator();
	
    public void onStart() {
        pluginHelp.addEntry("flip", "flip", "Flip a coin!");
        pluginHelp.addEntry("roll", "roll", "Roll a dice!");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.message = message;
        this.sender  = sender;
    }

    @Override
    public void run() {
        if (matchesReference("flip")) {
            final int side = new Generator().nextInt(0, 1);

            if (side == 0) {
                bot.notice(sender, "Coin flipped to tails.");
            } else if (side == 1) {
                bot.notice(sender, "Coin flipped to head.");
            }
        } else if (matchesReference("roll")) {
            bot.notice(sender, "Number rolled is " + generator.nextInt(1, 6));
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
