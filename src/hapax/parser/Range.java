package hapax.parser;

/**
 * Tuple for start, stop, and skipTo.  Used by nodes to tell {@link
 * hapax.Template} which sublists of the Template should be
 * processed.
 *
 * @author dcoker
 * @author jdp
 */
public final class Range {

    public final int start;
    public final int stop;
    public final int skipTo;


    public Range(int start, int stop, int skipTo) {
        this.start = start;
        this.stop = stop;
        this.skipTo = skipTo;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public int getSkipTo() {
        return skipTo;
    }
}
