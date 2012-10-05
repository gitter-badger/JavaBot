package javaBot.plugins;

import javaBot.JavaBot;
import javaBot.plugins.intl.javaBotPluginAbstract;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import wei2912.utilities.Generator;

@PluginImplementation
/** A plugin that allows you to roll a dice and flip a coin **/
public class Dice_Coin extends javaBotPluginAbstract {

	static JavaBot	bot;
	static String	message;
	static String	sender;

	@Override
	public void init(JavaBot bot, String message, String channel, String sender) {
		Dice_Coin.bot = bot;
		Dice_Coin.message = message;
		Dice_Coin.sender = sender;
	}

	@Override
	public void run() {
		if (Dice_Coin.message.equalsIgnoreCase(JavaBot.getPrefix() + "flip")) {
			final int side = new Generator().nextInt(0, 1);

			if (side == 0) {
				Dice_Coin.bot
				        .notice(Dice_Coin.sender, "Coin flipped to tails.");
			}
			else if (side == 1) {
				Dice_Coin.bot.notice(Dice_Coin.sender, "Coin flipped to head.");
			}
		}

		else if (Dice_Coin.message.equalsIgnoreCase(JavaBot.getPrefix()
		        + "roll")) {
			Dice_Coin.bot.notice(Dice_Coin.sender, "Number rolled is "
			        + new Generator().nextInt(1, 6));
		}
	}

}
