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
public class Generator extends SecureRandom {
	
	private static final long	serialVersionUID	= 1L;
	static SecureRandom	      generator	         = new SecureRandom();
	static int	              resultInt;
	static long	              resultLong;

	/**
	 * Returns a randomly generated string with the specified number of
	 * characters.
	 */
	public String nextString(int characters) {
		final String chars = "0123456789abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final StringBuffer result = new StringBuffer("");

		for (int i = 0; i < characters; i++) {
			final int character = generator.nextInt(chars.length() - 1);
			result.append(chars.charAt(character));
		}

		return result.toString();
	}
	
	/**
	 * Generates a random positive integer
	 */
	public int nextPositiveInt() {
		Generator.resultInt = generator.nextInt();
		if (Generator.resultInt < 0) {
			Generator.resultInt -= Generator.resultInt * 2;
			Generator.resultInt -= Generator.resultInt * 2;    // Changes to positive
		}
		return Generator.resultInt;
	}
	
	/**
	 * Generates a random negative integer
	 */
	public int nextNegativeInt() {
		Generator.resultInt = generator.nextInt();
		if (Generator.resultInt > 0) {
			Generator.resultInt -= Generator.resultInt * 2;
		}
		return Generator.resultInt;
	}
	
	/**
	 * Returns a randomly generated integer within a range.
	 */
	public int nextInt(int LowerRange, int UpperRange) {
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
	public int nextInt(int LowerRange, int UpperRange, int interval) {
		do {
			Generator.resultInt = Generator.generator.nextInt(UpperRange + 1);
		}
		while (!((Generator.resultInt >= LowerRange) && ((Generator.resultInt % interval) == 0)));

		return Generator.resultInt;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
