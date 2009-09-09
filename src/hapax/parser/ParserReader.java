package hapax.parser;

/**
 * Line number string reader
 *
 * @author jdp
 */
public final class ParserReader
    extends java.lang.Object
    implements java.lang.CharSequence
{

    private char[] buffer;

    private int lno = 1;


    public ParserReader(String string){
        super();
        if (null != string && 0 != string.length())
            this.buffer = string.toCharArray();
    }


    public int lineNumber(){
        return this.lno;
    }
    public int length(){
        char[] buf = this.buffer;
        if (null != buf)
            return buf.length;
        else
            return 0;
    }
    public char charAt(int idx){
        char[] buf = this.buffer;
        if (null != buf){
            if (-1 < idx && idx < buf.length)
                return buf[idx];
            else
                throw new IndexOutOfBoundsException(String.valueOf(idx)+":{"+buf.length+'}');
        }
        else
            throw new IndexOutOfBoundsException(String.valueOf(idx)+":{0}");
    }
    public int indexOf(String s){
        if (null != s && 0 != s.length()){
            char[] search = s.toCharArray();
            char[] buf = this.buffer;
            if (null != buf){
                int sc = 0;
                int scc = search.length;
                int idx = -1;
                for (int bc = 0, bcc = buf.length; bc < bcc; bc++){

                    if (buf[bc] == search[sc++]){

                        if (-1 == idx)
                            idx = bc;

                        if (sc >= scc)
                            return idx;
                        else
                            continue;
                    }
                    else {
                        idx = -1;
                        sc = 0;
                        continue;
                    }
                }
            }
            return -1;
        }
        else
            throw new IllegalArgumentException(s);
    }
    /**
     * @param start Offset index, inclusive
     * @param end Offset index, exclusive
     */
    public String delete(int start, int end){
        char[] buf = this.buffer;
        if (null != buf){
            int buflen = buf.length;
            if ((-1 < start && start < buflen)&&(start <= end && end <= buflen)){
                if (start == end)
                    return "";
                else {
                    int relen = (end-start);
                    char[] re = new char[relen];
                    System.arraycopy(buf,start,re,0,relen);
                    int nblen = (buflen-relen);
                    if (0 == nblen)
                        this.buffer = null;
                    else {
                        int term = (buflen-1);
                        char[] nb = new char[nblen];
                        if (0 == start){
                            /*
                             * Copy buffer tail to new buffer
                             */
                            System.arraycopy(buf,end,nb,0,nblen);
                            this.buffer = nb;
                        }
                        else if (term == end){
                            /*
                             * Copy buffer head to new buffer
                             */
                            System.arraycopy(buf,0,nb,0,nblen);
                            this.buffer = nb;
                        }
                        else {
                            /*
                             * Copy buffer head & tail to new buffer
                             */
                            int nbalen = start;
                            int nbblen = buflen-end;
                            System.arraycopy(buf,0,nb,0,nbalen);
                            System.arraycopy(buf,end,nb,nbalen,nbblen);
                            this.buffer = nb;
                        }
                    }
                    return this.lines(re,0,relen);
                }
            }
            else
                throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{"+buf.length+'}');
        }
        else
            throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{0}");
    }
    public String truncate(){
        char[] re = this.buffer;
        this.buffer = null;
        return this.lines(re,0,-1);
    }
    public CharSequence subSequence(int start, int end){
        char[] buf = this.buffer;
        if (null != buf){
            int buflen = buf.length;
            if ((-1 < start && start < buflen)&&(start <= end && end <= buflen)){
                if (start == end)
                    return "";
                else {
                    int relen = (end-start);
                    char[] re = new char[relen];
                    System.arraycopy(buf,start,re,0,relen);
                    return new String(re,0,relen);
                }
            }
            else
                throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{"+buf.length+'}');
        }
        else
            throw new IndexOutOfBoundsException(String.valueOf(start)+':'+String.valueOf(start)+":{0}");
    }
    public String toString(){
        char[] buf = this.buffer;
        if (null != buf)
            return new String(buf,0,buf.length);
        else
            return "";
    }

    protected String lines(char[] re, int ofs, int len){
        if (-1 == len)
            len = ((null != re)?(re.length):(0));

        this.lno += CountLines(re,ofs,len);

        return new String(re,ofs,len);
    }
    protected static int CountLines(char[] re, int ofs, int len){
        int num = 0;
        for (; ofs < len; ofs++){
            if ('\n' == re[ofs])
                num += 1;
        }
        return num;
    }
}
