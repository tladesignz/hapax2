package hapax;

/**
 * An Exception that is thrown by {@link Template} when something goes awry.
 *
 * @author dcoker
 */
public class TemplateException
    extends Exception
{

    public TemplateException(Throwable throwable) {
        super(throwable);
    }
    public TemplateException(String m, Throwable t) {
        super(m,t);
    }
    public TemplateException(String message) {
        super(message);
    }
}
