package alda.t9;

public final class EncodedMessage<HeaderType, MessageType> {
    /**
     * Information needed to decode the three, i.e. the huffman tree in some
     * suitable format.
     */
    public final HeaderType header;

    /**
     * The encoded message. The format is free, but a recommendation is to use a
     * string that simulates a binary representation using '1' and '0'. This has the
     * advantage that it's easy to read, but could be changed to a true binary
     * representation with only minor work.
     */
    public final MessageType message;

    public EncodedMessage(HeaderType header, MessageType message) {
        this.header = header;
        this.message = message;
    }

}
