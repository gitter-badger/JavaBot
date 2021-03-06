package org.jibble.pircbot;

/**
 * The Colors class provides several static fields and methods that you may find
 * useful when writing an IRC Bot.
 * <p>
 * This class contains constants that are useful for formatting lines sent to
 * IRC servers. These constants allow you to apply various formatting to the
 * lines, such as colours, boldness, underlining and reverse text.
 * <p>
 * The class contains static methods to remove colours and formatting from lines
 * of IRC text.
 * <p>
 * Here are some examples of how to use the contants from within a class that
 * extends PircBot and imports org.jibble.pircbot.*;
 *
 * <pre>
 * sendMessage("#cs", Colors.BOLD + "A bold hello!");
 *     <b>A bold hello!</b>
 * sendMessage("#cs", Colors.RED + "Red" + Colors.NORMAL + " text");
 *     <font color="red">Red</font> text
 * sendMessage("#cs", Colors.BOLD + Colors.RED + "Bold and red");
 *     <b><font color="red">Bold and red</font></b>
 * </pre>
 *
 * Please note that some IRC channels may be configured to reject any messages
 * that use colours. Also note that older IRC clients may be unable to correctly
 * display lines that contain colours and other control characters.
 * <p>
 * Note that this class name has been spelt in the American style in order to
 * remain consistent with the rest of the Java API.
 *
 * @author PircBot-PPF project
 * @version 1.0.0
 */
public class Colors {

    /**
     * Black coloured text.
     */
    public static final String BLACK = "\u000301";

    /**
     * Blue coloured text.
     */
    public static final String BLUE = "\u000312";

    /**
     * Bold text.
     */
    public static final String BOLD = "\u0002";

    /**
     * Brown coloured text.
     */
    public static final String BROWN = "\u000305";

    /**
     * Cyan coloured text.
     */
    public static final String CYAN = "\u000311";

    /**
     * Dark blue coloured text.
     */
    public static final String DARK_BLUE = "\u000302";

    /**
     * Dark gray coloured text.
     */
    public static final String DARK_GRAY = "\u000314";

    /**
     * Dark green coloured text.
     */
    public static final String DARK_GREEN = "\u000303";

    /**
     * Green coloured text.
     */
    public static final String GREEN = "\u000309";

    /**
     * Light gray coloured text.
     */
    public static final String LIGHT_GRAY = "\u000315";

    /**
     * Magenta coloured text.
     */
    public static final String MAGENTA = "\u000313";

    /**
     * Removes all previously applied color and formatting attributes.
     */
    public static final String NORMAL = "\u000f";

    /**
     * Olive coloured text.
     */
    public static final String OLIVE = "\u000307";

    /**
     * Purple coloured text.
     */
    public static final String PURPLE = "\u000306";

    /**
     * Red coloured text.
     */
    public static final String RED = "\u000304";

    /**
     * Reversed text (may be rendered as italic text in some clients).
     */
    public static final String REVERSE = "\u0016";

    /**
     * Teal coloured text.
     */
    public static final String TEAL = "\u000310";

    /**
     * Underlined text.
     */
    public static final String UNDERLINE = "\u001f";

    /**
     * White coloured text.
     */
    public static final String WHITE = "\u000300";

    /**
     * Yellow coloured text.
     */
    public static final String YELLOW = "\u000308";

    /**
     * This class should not be constructed.
     */
    private Colors() {}

    /**
     * Removes all colours from a line of IRC text.
     *
     * @param line
     *            the input text.
     * @return the same text, but with all colours removed.
     */
    public static String removeColors(String line) {
        boolean formattingFound = false;

        if ((line.indexOf(Colors.BOLD) >= 0) || (line.indexOf(Colors.UNDERLINE) >= 0)
                || (line.indexOf(Colors.REVERSE) >= 0)) {
            formattingFound = true;
        }

        final int          length = line.length();
        final StringBuffer buffer = new StringBuffer();
        int                i      = 0;

        while (i < length) {
            char ch = line.charAt(i);

            if (ch == '\u0003') {
                i++;

                // Skip "x" or "xy" (foreground color).
                if (i < length) {
                    ch = line.charAt(i);

                    if (Character.isDigit(ch)) {
                        i++;

                        if (i < length) {
                            ch = line.charAt(i);

                            if (Character.isDigit(ch)) {
                                i++;
                            }
                        }

                        // Now skip ",x" or ",xy" (background color).
                        if (i < length) {
                            ch = line.charAt(i);

                            if (ch == ',') {
                                i++;

                                if (i < length) {
                                    ch = line.charAt(i);

                                    if (Character.isDigit(ch)) {
                                        i++;

                                        if (i < length) {
                                            ch = line.charAt(i);

                                            if (Character.isDigit(ch)) {
                                                i++;
                                            }
                                        }
                                    } else {

                                        // Keep the comma.
                                        i--;
                                    }
                                } else {

                                    // Keep the comma.
                                    i--;
                                }
                            }
                        }
                    }
                }
            } else if (ch == '\u000f') {
                if (formattingFound) {
                    buffer.append(ch);
                }

                i++;
            } else {
                buffer.append(ch);
                i++;
            }
        }

        return buffer.toString();
    }

    /**
     * Remove formatting from a line of IRC text.
     *
     * @param line
     *            the input text.
     * @return the same text, but without any bold, underlining, reverse, etc.
     */
    public static String removeFormatting(String line) {
        boolean coloursFound = false;

        if (line.indexOf('\u0003') >= 0) {
            coloursFound = true;
        }

        final int          length = line.length();
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < length; i++) {
            final char ch = line.charAt(i);

            if (((ch == '\u000f') &&!coloursFound) || (ch == '\u0002') || (ch == '\u001f') || (ch == '\u0016')) {

                // Don't add this character.
            } else {
                buffer.append(ch);
            }
        }

        return buffer.toString();
    }

    /**
     * Removes all formatting and colours from a line of IRC text.
     *
     * @param line
     *            the input text.
     * @return the same text, but without formatting and colour characters.
     */
    public static String removeFormattingAndColors(String line) {
        return Colors.removeFormatting(Colors.removeColors(line));
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
