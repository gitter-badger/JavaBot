package org.jibble.pircbot;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class FallbackInputStreamReader extends Reader {

    /**
     * Turn this on for low level debugging output
     */
    private static final boolean debug = false;

    // true,
    // fillBuffer()
    // will
    // not
    // try to
    // read
    // more
    private ByteBuffer buf = ByteBuffer.allocate(1024);    // if

    // much
    // of buf
    // is
    // filled
    // (position
    // in
    // buf) -
    // always
    // kept
    // up to
    // date,
    // even
    // inside
    // method
    // calls
    private byte[] tmp = new byte[1024];

    // null
    // after
    // call
    // to
    // fillBuffer(),
    // no
    // more
    // data
    // available
    private int               bufFillLevel;    // how
    private CharBuffer        decBuf;          // if
    private final Charset     fallbackCharset;
    private boolean           gotEof;          // if
    private final InputStream in;

    // null
    // after
    // call
    // to
    // fillDecodeBuffer(),
    // no
    // more
    // data
    // available
    private Charset             lastCharset;
    private final PircBotLogger logger;
    private final Charset       primaryCharset;

    public FallbackInputStreamReader(PircBotLogger logger, InputStream in, String primaryCharsetName,
                                     String fallbackCharsetName)
            throws UnsupportedEncodingException {
        this.logger          = logger;
        this.in              = in;
        this.primaryCharset  = Charset.forName(primaryCharsetName);
        this.fallbackCharset = Charset.forName(fallbackCharsetName);
        this.buf.limit(0);
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        this.fillDecodeBuffer();

        if (this.decBuf == null) {
            return -1;
        }

        final int rem = this.decBuf.remaining();

        if (rem < len) {
            len = rem;
        }

        this.decBuf.get(cbuf, off, len);

        if (FallbackInputStreamReader.debug) {
            this.logger.log(this.lastCharset + " READ return '" + new String(cbuf, off, len) + "'");
        }

        return len;
    }

    private void fillDecodeBuffer() throws IOException {
        if ((this.decBuf != null) && this.decBuf.hasRemaining()) {
            return;
        }

        this.fillBuffer();

        if (this.buf == null) {
            this.decBuf = null;

            return;
        }

        CharsetDecoder d = this.primaryCharset.newDecoder();

        d.onMalformedInput(CodingErrorAction.REPORT);
        d.onUnmappableCharacter(CodingErrorAction.REPORT);

        final int origPos = this.buf.position();

        try {
            this.decBuf      = d.decode(this.buf);
            this.lastCharset = this.primaryCharset;
        } catch (final IOException e) {
            d = this.fallbackCharset.newDecoder();
            d.onMalformedInput(CodingErrorAction.REPLACE);
            d.onUnmappableCharacter(CodingErrorAction.REPLACE);

            try {

                // rewind
                this.buf.position(origPos);
                this.decBuf      = d.decode(this.buf);
                this.lastCharset = this.fallbackCharset;
            } catch (final CharacterCodingException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    private void fillBuffer() throws IOException {
        if (FallbackInputStreamReader.debug) {
            this.logger.log((this.buf == null)
                            ? "FISR bp null bl null fl n/a"
                            : ("bp " + this.buf.position() + " bl " + this.buf.limit() + " fl " + this.bufFillLevel));
        }

        // if we got EOF, just give everything we got left, if any
        if (this.gotEof) {
            if ((this.buf != null) &&!this.buf.hasRemaining()) {
                this.buf = null;
            }

            if (FallbackInputStreamReader.debug) {
                this.logger.log("FISR  -> EOF");
            }

            return;
        }

        // sanity check
        if ((this.buf != null) && this.buf.hasRemaining()) {

            // this should never happen
            this.logger.log("### BUG in FISR: pos<lim: " + this.buf.position() + " < " + this.buf.limit());

            // discard remaining
            this.buf.position(this.buf.limit());
        }

        // compact remaining data
        this.buf.limit(this.bufFillLevel);
        this.buf.compact();
        this.bufFillLevel = this.buf.position();

        // see if we got a line feed already last round
        if (this.checkLF(0)) {
            return;
        }

        try {
            while (true) {

                // read some more data
                final int r = this.in.read(this.tmp);

                // if we got EOF, just toss the remaining data back and remember
                // EOF happened
                if (r == -1) {
                    this.gotEof = true;
                    this.tmp    = null;
                    this.buf.flip();

                    if (FallbackInputStreamReader.debug) {
                        this.logger.log("FISR  -> 2 bp " + this.buf.position() + " bl " + this.buf.limit() + " fl "
                                        + this.bufFillLevel);
                    }

                    if (this.buf.remaining() == 0) {
                        this.buf = null;
                    }

                    return;
                }

                // make sure we have space to receive the data
                if (this.buf.remaining() < r) {
                    if (FallbackInputStreamReader.debug) {
                        this.logger.log("FISR bp Doubling buffer");
                    }

                    this.buf.flip();

                    final ByteBuffer bb = ByteBuffer.allocate(this.buf.capacity() * 2);

                    bb.put(this.buf);
                    this.buf = bb;
                }

                // copy the newly received data to the buffer
                final int start = this.buf.position();

                this.buf.put(this.tmp, 0, r);
                this.bufFillLevel = this.buf.position();

                // see if we got a line feed
                if (this.checkLF(start)) {
                    return;
                }
            }
        } catch (final IOException e) {

            // this might happen non-fatally, for example
            // SocketTimeoutException, so just update state so we can continue
            // later.
            // set limit to 0 since nothing of what may be in the buffer will
            // get consumed this round. This way next fillBuffer() call will
            // continue where we left
            this.buf.limit(0);

            if (FallbackInputStreamReader.debug) {
                this.logger.log("FISR  -> " + e + " bp " + this.buf.position() + " bl " + this.buf.limit() + " fl "
                                + this.bufFillLevel);
            }

            throw e;
        }
    }

    private boolean checkLF(int start) {
        for (int i = start; i < this.buf.position(); ++i) {
            if (this.buf.get(i) == '\n') {

                // do a flip, but only give away up until and including LF
                this.buf.position(0);
                this.buf.limit(i + 1);

                if (FallbackInputStreamReader.debug) {
                    this.logger.log("FISR  -> 1 / " + start + " bp " + this.buf.position() + " bl " + this.buf.limit()
                                    + " fl " + this.bufFillLevel);
                }

                return true;
            }
        }

        return false;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
