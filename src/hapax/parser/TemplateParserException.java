package hapax.parser;

import hapax.TemplateException;

/**
 * An exception thrown by a TemplateParser.
 *
 * @author dcoker
 */
public class TemplateParserException
    extends TemplateException
{

    public TemplateParserException(String details) {
        super(details);
    }
}
