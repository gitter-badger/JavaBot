package org.jibble.pircbot;

//~--- JDK imports ------------------------------------------------------------

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is used to process DCC events from the server.
 *
 * @author PircBot-PPF project
 * @version 1.0.0
 */
public class DccManager {
    private final Vector  _awaitingResume = new Vector();
    private final PircBot _bot;

    /**
     * Constructs a DccManager to look after all DCC SEND and CHAT events.
     *
     * @param bot
     *            The PircBot whose DCC events this class will handle.
     */
    DccManager(PircBot bot) {
        this._bot = bot;
    }

    /**
     * Processes a DCC request.
     *
     * @return True if the type of request was handled successfully.
     */
    boolean processRequest(String nick, String login, String hostname, String request) {
        final StringTokenizer tokenizer = new StringTokenizer(request);

        tokenizer.nextToken();

        final String type     = tokenizer.nextToken();
        final String filename = tokenizer.nextToken();

        if (type.equals("SEND")) {
            final long address = Long.parseLong(tokenizer.nextToken());
            final int  port    = Integer.parseInt(tokenizer.nextToken());
            long       size    = -1;

            try {
                size = Long.parseLong(tokenizer.nextToken());
            } catch (final Exception e) {

                // Stick with the old value.
            }

            final DccFileTransfer transfer = new DccFileTransfer(this._bot, this, nick, login, hostname, type,
                                                 filename, address, port, size);

            this._bot.onIncomingFileTransfer(transfer);
        } else if (type.equals("RESUME")) {
            final int       port     = Integer.parseInt(tokenizer.nextToken());
            final long      progress = Long.parseLong(tokenizer.nextToken());
            DccFileTransfer transfer = null;

            synchronized (this._awaitingResume) {
                for (int i = 0; i < this._awaitingResume.size(); i++) {
                    transfer = (DccFileTransfer) this._awaitingResume.elementAt(i);

                    if (transfer.getNick().equals(nick) && (transfer.getPort() == port)) {
                        this._awaitingResume.removeElementAt(i);

                        break;
                    }
                }
            }

            if (transfer != null) {
                transfer.setProgress(progress);
                this._bot.sendCTCPCommand(nick, "DCC ACCEPT file.ext " + port + " " + progress);
            }
        } else if (type.equals("ACCEPT")) {
            final int       port     = Integer.parseInt(tokenizer.nextToken());
            DccFileTransfer transfer = null;

            synchronized (this._awaitingResume) {
                for (int i = 0; i < this._awaitingResume.size(); i++) {
                    transfer = (DccFileTransfer) this._awaitingResume.elementAt(i);

                    if (transfer.getNick().equals(nick) && (transfer.getPort() == port)) {
                        this._awaitingResume.removeElementAt(i);

                        break;
                    }
                }
            }

            if (transfer != null) {
                transfer.doReceive(transfer.getFile(), true);
            }
        } else if (type.equals("CHAT")) {
            final long    address = Long.parseLong(tokenizer.nextToken());
            final int     port    = Integer.parseInt(tokenizer.nextToken());
            final DccChat chat    = new DccChat(this._bot, nick, login, hostname, address, port);

            new Thread() {
                @Override
                public void run() {
                    DccManager.this._bot.onIncomingChatRequest(chat);
                }
            }.start();
        } else {
            return false;
        }

        return true;
    }

    /**
     * Add this DccFileTransfer to the list of those awaiting possible resuming.
     *
     * @param transfer
     *            the DccFileTransfer that may be resumed.
     */
    void addAwaitingResume(DccFileTransfer transfer) {
        synchronized (this._awaitingResume) {
            this._awaitingResume.addElement(transfer);
        }
    }

    /**
     * Remove this transfer from the list of those awaiting resuming.
     */
    void removeAwaitingResume(DccFileTransfer transfer) {
        this._awaitingResume.removeElement(transfer);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
