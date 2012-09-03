package wei2912.utilities;

// ~--- JDK imports ------------------------------------------------------------

import java.security.SecureRandom;

/**
 * Some friendlier methods for generating numbers. Only integer values are
 * supported for now. All numbers are generated using the SecureRandom class, so
 * all numbers are secure enough for cryptography. If you need a number for
 * cryptography, a number generated using the generateInt method is highly
 * recommended.
 * 
 * @author wei2912
 * @version 1.0
 */
public final class Generator extends SecureRandom {
	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;
	static SecureRandom	      generator	         = new SecureRandom();
	static int	              resultInt;
	static long	              resultLong;

	/**
	 * Generates a random integer.
	 */
	public static int generateInt() {
		Generator.resultInt = Generator.generator.nextInt();

		return Generator.resultInt;
	}

	/**
	 * Generates a random positive integer
	 */
	public static int generatePositiveInt() {
		Generator.resultInt = Generator.generator.nextInt();

		if (Generator.resultInt < 0) {
			Generator.resultInt -= Generator.resultInt * 2;    // Changes to
															// positive
		}

		return Generator.resultInt;
	}

	/**
	 * Returns a randomly generated integer within 0 and int UpperRange.
	 */
	public static int generateInt(int UpperRange) {
		do {
			Generator.resultInt = Generator.generator.nextInt(UpperRange + 1);
		}
		while (!(Generator.resultInt >= 0));

		return Generator.resultInt;
	}

	/**
	 * Returns a randomly generated integer within a range.
	 */
	public static int generateInt(int LowerRange, int UpperRange) {
		do {
			Generator.resultInt = Generator.generator.nextInt(UpperRange + 1);
		}
		while (!(Generator.resultInt >= LowerRange));

		return Generator.resultInt;
	}

	/**
	 * Returns a randomly generated integer within a range and between
	 * intervals.
	 */
	public static int generateInt(int LowerRange, int UpperRange, int interval) {
		do {
			Generator.resultInt = Generator.generator.nextInt(UpperRange + 1);
		}
		while (!((Generator.resultInt >= LowerRange) && ((Generator.resultInt % interval) == 0)));

		return Generator.resultInt;
	}

	/**
	 * Returns a randomly generated prime.
	 */
	public static long generatePrime(int limit) {
		long x, y, c = 0;
		for (x = 2; x < limit; x++) {
			if (((x % 2) != 0) || (x == 2)) {
				for (y = 2; y <= (x / 2); y++) {
					if ((x % y) == 0) {
						break;
					}
				}

				if (y > (x / 2)) {
					c++;
				}
			}
		}

		return c;
	}

	/**
	 * Returns a randomly generated prime with a bottom limit
	 */
	public static long generatePrime(int LowerLimit, int UpperLimit) {
		long x, y, c = 0;
		for (x = LowerLimit; x < UpperLimit; x++) {
			if (((x % 2) != 0) || (x == 2)) {
				for (y = 2; y <= (x / 2); y++) {
					if ((x % y) == 0) {
						break;
					}
				}

				if (y > (x / 2)) {
					c++;
				}
			}
		}

		return c;
	}

	/**
	 * Returns a randomly generated string with the specified number of
	 * characters.
	 */
	public static String generateString(int characters) {
		final String chars = "0123456789abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final StringBuffer result = new StringBuffer("");

		for (int i = 0; i < characters; i++) {
			final int character = Generator.generateInt(chars.length() - 1);
			result.append(chars.charAt(character));
		}

		return result.toString();
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
