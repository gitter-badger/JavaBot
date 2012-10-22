package javaBot.plugins;

//~--- non-JDK imports --------------------------------------------------------

import javaBot.Commands;
import javaBot.JavaBot;

import javaBot.plugins.intl.javaBotPluginAbstract;
import javaBot.plugins.intl.pluginHelp;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.jibble.pircbot.Colors;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.util.regex.Pattern;

@PluginImplementation

/** Variation of TicTacToe */
public class TicTacToe extends javaBotPluginAbstract {
    static char field[][] = {
        { ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ' },
        { ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ' },
    };
    final static int lose        = 2;
    static String    player1Lock = null;
    static String    player2Lock = null;
    static boolean   tictactoe   = false;
    static boolean   Bturn       = false;
    static boolean   Aturn       = false;

    // Constants
    final static int win = 5;

    public void onStart() {
        pluginHelp.addEntry("t3", "t3", "Starts a game of Tic Tac Toe, 5x5 board.");
    }

    @Override
    public void init(JavaBot bot, String message, String channel, String sender) {
        this.bot     = bot;
        this.channel = channel;
        this.sender  = sender;
        this.message = message;
    }

    @Override
    public void run() {
        if (this.message.equalsIgnoreCase(JavaBot.getPrefix() + "t3")) {

            // Empty the array
            for (int a = 0; a < 5; a++) {
                for (int b = 0; b < 5; b++) {
                    TicTacToe.field[a][b] = ' ';    // Empties the array
                }
            }

            this.bot.sendMessage(
                this.channel,
                this.sender
                + " has created a game of tic tac toe with a board of 5x5. To win, you need to get 4 in a row.");
            this.bot.sendMessage(this.channel,
                                      "Type " + JavaBot.getPrefix()
                                      + "t3 <row><column> to join in after he makes his move. An example is "
                                      + Colors.BOLD + JavaBot.getPrefix() + "t3 C3" + Colors.NORMAL
                                      + " for column C, row 3.");
            this.bot.sendMessage(this.channel, "The first person to join in gets to play with him.");
            this.printField(this.channel, true);
            this.bot.notice(this.sender, "Waiting for a person to join you...");
            TicTacToe.tictactoe   = true;
            TicTacToe.Aturn       = false;
            TicTacToe.Bturn       = true;
            TicTacToe.player1Lock = this.sender;    // Locks the player
        } else if (this.message.startsWith(JavaBot.getPrefix() + "t3 ")) {
            this.move(this.message, this.sender);
        }
    }

    public void move(String message, String sender) {
        this.sender = sender;

        if (TicTacToe.tictactoe == true) {
            final String            parameter = Commands.checkParameter(message)[0];
            Pattern                 pattern   = null;
            java.util.regex.Matcher matcher   = null;

            try {
                pattern = Pattern.compile("[A-E][1-5]");
                matcher = pattern.matcher(parameter);
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (!matcher.matches()) {
                this.bot.notice(sender, "Invalid move.");
            } else {
                int a = 0;
                int b = 0;

                if (parameter.charAt(0) == 'A') {
                    a = 0;
                } else if (parameter.charAt(0) == 'B') {
                    a = 1;
                } else if (parameter.charAt(0) == 'C') {
                    a = 2;
                } else if (parameter.charAt(0) == 'D') {
                    a = 3;
                } else if (parameter.charAt(0) == 'E') {
                    a = 4;
                }

                if (parameter.charAt(1) == '1') {
                    b = 0;
                } else if (parameter.charAt(1) == '2') {
                    b = 1;
                } else if (parameter.charAt(1) == '3') {
                    b = 2;
                } else if (parameter.charAt(1) == '4') {
                    b = 3;
                } else if (parameter.charAt(1) == '5') {
                    b = 4;
                }

                if (TicTacToe.Aturn && sender.equals(TicTacToe.player1Lock)) {
                    if (TicTacToe.field[a][b] == ' ') {
                        TicTacToe.field[a][b] = 'X';
                        TicTacToe.Aturn       = false;
                        TicTacToe.Bturn       = true;
                        this.printField(TicTacToe.player1Lock);

                        if (TicTacToe.player2Lock != null) {
                            this.bot.notice(TicTacToe.player2Lock, "It's your turn.");
                            this.printField(TicTacToe.player2Lock);
                        }
                    } else {
                        this.bot.notice(sender, "Invalid move.");
                    }
                } else if (TicTacToe.Bturn) {
                    if (TicTacToe.player2Lock == null) {
                        if (TicTacToe.player1Lock.equals(TicTacToe.player2Lock)) {
                            this.bot.notice(
                                TicTacToe.player1Lock,
                                "Please be patient and wait for a person to join you. You may not play a game with yourself.");
                        } else {
                            TicTacToe.player2Lock = sender;
                            this.bot.notice(TicTacToe.player2Lock, "You've successfully joined!");

                            if (TicTacToe.field[a][b] == ' ') {
                                TicTacToe.field[a][b] = 'O';
                                TicTacToe.Aturn       = true;
                                TicTacToe.Bturn       = false;
                                this.bot.notice(TicTacToe.player1Lock, "It's your turn.");
                                this.printField(TicTacToe.player1Lock);
                                this.printField(TicTacToe.player2Lock);
                            } else {
                                this.bot.notice(sender, "Invalid move.");
                            }
                        }
                    } else if (sender.equals(TicTacToe.player2Lock)) {
                        if (TicTacToe.field[a][b] == ' ') {
                            TicTacToe.field[a][b] = 'O';
                            TicTacToe.Aturn       = true;
                            TicTacToe.Bturn       = false;
                            this.bot.notice(TicTacToe.player1Lock, "It's your turn.");
                            this.printField(TicTacToe.player1Lock);
                            this.printField(TicTacToe.player2Lock);
                        } else {
                            this.bot.notice(sender, "Invalid move.");
                        }
                    } else {
                        this.bot.notice(sender, "Invalid move.");
                    }
                } else {
                    this.bot.notice(sender, "Invalid move.");
                }

                // X wins
                if (this.win('X')) {
                    this.bot.notice(this.channel, TicTacToe.player1Lock + ": You've won!");
                    this.bot.notice(this.channel, "Results (X is the winner):");
                    this.printField(this.channel);
                    TicTacToe.player1Lock = null;
                    TicTacToe.player2Lock = null;
                    TicTacToe.tictactoe   = false;
                }

                // O wins
                else if (this.win('O')) {
                    this.bot.sendMessage(this.channel, TicTacToe.player2Lock + ": You've won!");
                    this.bot.sendMessage(this.channel, "Results (O is the winner):");
                    this.printField(this.channel, true);
                    TicTacToe.player1Lock = null;
                    TicTacToe.player2Lock = null;
                    TicTacToe.tictactoe   = false;
                }

                // No more winning moves.
                else if (!this.win(' ')) {
                    this.bot.sendMessage(this.channel,
                                              "It's a draw between " + TicTacToe.player1Lock + " and player2Lock"
                                              + "!");
                    this.printField(this.channel, true);
                    TicTacToe.player1Lock = null;
                    TicTacToe.player2Lock = null;
                    TicTacToe.tictactoe   = false;
                }
            }
        } else {
            this.bot.notice(sender,
                                 "Please type " + JavaBot.getPrefix()
                                 + "t3 without any parameters to start a new game.");
        }
    }

    public void printField(String name) {
        this.bot.notice(name, " |1|2|3|4|5|");
        this.bot.notice(name,
                             "A|" + TicTacToe.field[0][0] + "|" + TicTacToe.field[0][1] + "|" + TicTacToe.field[0][2]
                             + "|" + TicTacToe.field[0][3] + "|" + TicTacToe.field[0][4] + "|");
        this.bot.notice(name,
                             "B|" + TicTacToe.field[1][0] + "|" + TicTacToe.field[1][1] + "|" + TicTacToe.field[1][2]
                             + "|" + TicTacToe.field[1][3] + "|" + TicTacToe.field[1][4] + "|");
        this.bot.notice(name,
                             "C|" + TicTacToe.field[2][0] + "|" + TicTacToe.field[2][1] + "|" + TicTacToe.field[2][2]
                             + "|" + TicTacToe.field[2][3] + "|" + TicTacToe.field[2][4] + "|");
        this.bot.notice(name,
                             "D|" + TicTacToe.field[3][0] + "|" + TicTacToe.field[3][1] + "|" + TicTacToe.field[3][2]
                             + "|" + TicTacToe.field[3][3] + "|" + TicTacToe.field[3][4] + "|");
        this.bot.notice(name,
                             "E|" + TicTacToe.field[4][0] + "|" + TicTacToe.field[4][1] + "|" + TicTacToe.field[4][2]
                             + "|" + TicTacToe.field[4][3] + "|" + TicTacToe.field[4][4] + "|");
    }

    public void printField(String name, boolean sendMessage) {
        if (sendMessage == true) {
            this.bot.sendMessage(name, " |1|2|3|4|5|");
            this.bot.sendMessage(name,
                                      "A|" + TicTacToe.field[0][0] + "|" + TicTacToe.field[0][1] + "|"
                                      + TicTacToe.field[0][2] + "|" + TicTacToe.field[0][3] + "|"
                                      + TicTacToe.field[0][4] + "|");
            this.bot.sendMessage(name,
                                      "B|" + TicTacToe.field[1][0] + "|" + TicTacToe.field[1][1] + "|"
                                      + TicTacToe.field[1][2] + "|" + TicTacToe.field[1][3] + "|"
                                      + TicTacToe.field[1][4] + "|");
            this.bot.sendMessage(name,
                                      "C|" + TicTacToe.field[2][0] + "|" + TicTacToe.field[2][1] + "|"
                                      + TicTacToe.field[2][2] + "|" + TicTacToe.field[2][3] + "|"
                                      + TicTacToe.field[2][4] + "|");
            this.bot.sendMessage(name,
                                      "D|" + TicTacToe.field[3][0] + "|" + TicTacToe.field[3][1] + "|"
                                      + TicTacToe.field[3][2] + "|" + TicTacToe.field[3][3] + "|"
                                      + TicTacToe.field[3][4] + "|");
            this.bot.sendMessage(name,
                                      "E|" + TicTacToe.field[4][0] + "|" + TicTacToe.field[4][1] + "|"
                                      + TicTacToe.field[4][2] + "|" + TicTacToe.field[4][3] + "|"
                                      + TicTacToe.field[4][4] + "|");
        } else {
            this.bot.notice(name, " |1|2|3|4|5|");
            this.bot.notice(name,
                                 "A|" + TicTacToe.field[0][0] + "|" + TicTacToe.field[0][1] + "|"
                                 + TicTacToe.field[0][2] + "|" + TicTacToe.field[0][3] + "|" + TicTacToe.field[0][4]
                                 + "|");
            this.bot.notice(name,
                                 "B|" + TicTacToe.field[1][0] + "|" + TicTacToe.field[1][1] + "|"
                                 + TicTacToe.field[1][2] + "|" + TicTacToe.field[1][3] + "|" + TicTacToe.field[1][4]
                                 + "|");
            this.bot.notice(name,
                                 "C|" + TicTacToe.field[2][0] + "|" + TicTacToe.field[2][1] + "|"
                                 + TicTacToe.field[2][2] + "|" + TicTacToe.field[2][3] + "|" + TicTacToe.field[2][4]
                                 + "|");
            this.bot.notice(name,
                                 "D|" + TicTacToe.field[3][0] + "|" + TicTacToe.field[3][1] + "|"
                                 + TicTacToe.field[3][2] + "|" + TicTacToe.field[3][3] + "|" + TicTacToe.field[3][4]
                                 + "|");
            this.bot.notice(name,
                                 "E|" + TicTacToe.field[4][0] + "|" + TicTacToe.field[4][1] + "|"
                                 + TicTacToe.field[4][2] + "|" + TicTacToe.field[4][3] + "|" + TicTacToe.field[4][4]
                                 + "|");
        }
    }

    public boolean win(char character) {
        if    // Horizontal
                (((TicTacToe.field[0][0] == character) && (TicTacToe.field[0][1] == character) && (TicTacToe
                    .field[0][2] == character) && (TicTacToe.field[0][3] == character)) || ((TicTacToe
                    .field[0][1] == character) && (TicTacToe.field[0][2] == character) && (TicTacToe
                    .field[0][3] == character) && (TicTacToe.field[0][4] == character)) || ((TicTacToe
                    .field[1][0] == character) && (TicTacToe.field[1][1] == character) && (TicTacToe
                    .field[1][2] == character) && (TicTacToe.field[1][3] == character)) || ((TicTacToe
                    .field[1][1] == character) && (TicTacToe.field[1][2] == character) && (TicTacToe
                    .field[1][3] == character) && (TicTacToe.field[1][4] == character)) || ((TicTacToe
                    .field[2][0] == character) && (TicTacToe.field[2][1] == character) && (TicTacToe
                    .field[2][2] == character) && (TicTacToe.field[2][3] == character)) || ((TicTacToe
                    .field[2][1] == character) && (TicTacToe.field[2][2] == character) && (TicTacToe
                    .field[2][3] == character) && (TicTacToe.field[2][4] == character)) || ((TicTacToe
                    .field[3][0] == character) && (TicTacToe.field[3][1] == character) && (TicTacToe
                    .field[3][2] == character) && (TicTacToe.field[3][3] == character)) || ((TicTacToe
                    .field[3][1] == character) && (TicTacToe.field[3][2] == character) && (TicTacToe
                    .field[3][3] == character) && (TicTacToe.field[3][4] == character)) || ((TicTacToe
                    .field[4][0] == character) && (TicTacToe.field[4][1] == character) && (TicTacToe
                    .field[4][2] == character) && (TicTacToe.field[4][3] == character)) || ((TicTacToe
                    .field[4][1] == character) && (TicTacToe.field[4][2] == character) && (TicTacToe
                    .field[4][3] == character) && (TicTacToe.field[4][4] == character)) ||

        // Vertical
        ((TicTacToe.field[0][0] == character) && (TicTacToe.field[1][0] == character)
                && (TicTacToe.field[2][0] == character)
                && (TicTacToe.field[3][0] == character)) || ((TicTacToe.field[1][0] == character)
                    && (TicTacToe.field[2][0] == character) && (TicTacToe.field[3][0] == character)
                    && (TicTacToe.field[4][0] == character)) || ((TicTacToe.field[0][1] == character)
                        && (TicTacToe.field[1][1] == character) && (TicTacToe.field[2][1] == character)
                        && (TicTacToe.field[3][1] == character)) || ((TicTacToe.field[1][1] == character)
                            && (TicTacToe.field[2][1] == character) && (TicTacToe.field[3][1] == character)
                            && (TicTacToe.field[4][1] == character)) || ((TicTacToe.field[0][2] == character)
                                && (TicTacToe.field[1][2] == character) && (TicTacToe.field[2][2] == character)
                                && (TicTacToe.field[3][2] == character)) || ((TicTacToe.field[1][2] == character)
                                    && (TicTacToe.field[2][2] == character) && (TicTacToe.field[3][2] == character)
                                    && (TicTacToe.field[4][2] == character)) || ((TicTacToe.field[0][3] == character)
                                        && (TicTacToe.field[1][3] == character) && (TicTacToe.field[2][3] == character)
                                        && (TicTacToe.field[3][3] == character)) || ((TicTacToe.field[1][3]
                                            == character) && (TicTacToe.field[2][3] == character)
                                                && (TicTacToe.field[3][3] == character)
                                                    && (TicTacToe.field[4][3] == character)) || ((TicTacToe.field[0][4]
                                                        == character) && (TicTacToe.field[1][4] == character)
                                                            && (TicTacToe.field[2][4] == character)
                                                                && (TicTacToe.field[3][4]
                                                                    == character)) || ((TicTacToe.field[1][4]
                                                                        == character) && (TicTacToe.field[2][4]
                                                                            == character) && (TicTacToe.field[3][4]
                                                                                == character) && (TicTacToe.field[4][4]
                                                                                    == character)) ||

        // Diagonal L to R
        ((TicTacToe.field[0][0] == character) && (TicTacToe.field[1][1] == character)
                && (TicTacToe.field[2][2] == character)
                && (TicTacToe.field[3][3] == character)) || ((TicTacToe.field[1][1] == character)
                    && (TicTacToe.field[2][2] == character) && (TicTacToe.field[3][3] == character)
                    && (TicTacToe.field[4][4] == character)) || ((TicTacToe.field[0][1] == character)
                        && (TicTacToe.field[1][2] == character) && (TicTacToe.field[2][3] == character)
                        && (TicTacToe.field[3][4] == character)) || ((TicTacToe.field[1][0] == character)
                            && (TicTacToe.field[2][1] == character) && (TicTacToe.field[3][2] == character)
                            && (TicTacToe.field[4][3] == character)) ||

        // Diagonal R to L
        ((TicTacToe.field[0][4] == character) && (TicTacToe.field[1][3] == character)
                && (TicTacToe.field[2][2] == character)
                && (TicTacToe.field[3][1] == character)) || ((TicTacToe.field[1][3] == character)
                    && (TicTacToe.field[2][2] == character) && (TicTacToe.field[3][1] == character)
                    && (TicTacToe.field[4][0] == character)) || ((TicTacToe.field[0][3] == character)
                        && (TicTacToe.field[1][2] == character) && (TicTacToe.field[2][1] == character)
                        && (TicTacToe.field[3][0] == character)) || ((TicTacToe.field[1][4] == character)
                            && (TicTacToe.field[2][3] == character) && (TicTacToe.field[3][2] == character)
                            && (TicTacToe.field[4][1] == character))) {
            return true;
        } else {
            return false;
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com
