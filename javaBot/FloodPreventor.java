package javaBot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FloodPreventor {
	static JavaBot	          bot;
	static String	          channel;
	static String	          sender;
	static String	          message;
	static long	              duration	    = 0;
	static long	              messageLimit	= 0;
	static long	              throttledTime	= 0;

	static ArrayList<String>	nicks	    = new ArrayList<String>();
	static ArrayList<Integer>	times	    = new ArrayList<Integer>();

	public FloodPreventor(JavaBot bot, String channel, String sender,
	        String message, long duration, long messageLimit, long throttledTime) {
		FloodPreventor.bot = bot;
		FloodPreventor.channel = channel;
		FloodPreventor.sender = sender;
		FloodPreventor.message = message;
		FloodPreventor.duration = duration;
		FloodPreventor.messageLimit = messageLimit;
		FloodPreventor.throttledTime = throttledTime;
	}

	public void run() {
		if (FloodPreventor.nicks.contains(FloodPreventor.sender)) {
			FloodPreventor.times.set(FloodPreventor.nicks
			        .indexOf(FloodPreventor.sender),
			        FloodPreventor.times.get(FloodPreventor.nicks
			                .indexOf(FloodPreventor.sender)) + 1);
		}
		else {
			FloodPreventor.nicks.add(FloodPreventor.sender);
			FloodPreventor.times.add(1);

			final int index = FloodPreventor.nicks
			        .indexOf(FloodPreventor.sender);

			final Timer remover = new Timer();

			class removeTimes extends TimerTask {
				@Override
				public void run() {
					if ((FloodPreventor.nicks.size() > 0)
					        && (index < FloodPreventor.nicks.size())) {
						if (FloodPreventor.nicks.get(index).equals(
						        FloodPreventor.sender)) {
							FloodPreventor.nicks.remove(index);
							FloodPreventor.times.remove(index);
						}
					}
				}
			}

			remover.schedule(new removeTimes(), FloodPreventor.duration);
		}

		String victim;

		for (int i = 0; i < FloodPreventor.times.size(); i++) {
			if (FloodPreventor.times.get(i) >= FloodPreventor.messageLimit) {
				FloodPreventor.bot.deVoice(FloodPreventor.channel,
				        FloodPreventor.nicks.get(i));

				double temp1 = 0.0;
				final double temp2 = FloodPreventor.throttledTime;

				temp1 = temp2 / 1000;

				FloodPreventor.bot.notice(FloodPreventor.nicks.get(i),
				        "You've been throttled for " + temp1 + " seconds at "
				                + FloodPreventor.channel + ".");

				victim = FloodPreventor.nicks.get(i);

				if ((FloodPreventor.nicks.size() > 0)
				        && (i < FloodPreventor.nicks.size())) {
					if (FloodPreventor.nicks.get(i).equals(
					        FloodPreventor.sender)) {
						FloodPreventor.nicks.remove(i);
						FloodPreventor.times.remove(i);
					}
				}

				final Timer throttledDuration = new Timer();

				class throttled extends TimerTask {
					String	victim	= "";

					throttled(String victim) {
						this.victim = victim;
					}

					@Override
					public void run() {
						FloodPreventor.bot.voice(FloodPreventor.channel,
						        this.victim);
						FloodPreventor.bot.notice(this.victim,
						        "You've been unthrottled at "
						                + FloodPreventor.channel + ".");
					}
				}

				throttledDuration.schedule(new throttled(victim),
				        FloodPreventor.throttledTime);
			}
		}
	}
}
