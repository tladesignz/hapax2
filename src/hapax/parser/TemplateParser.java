package hapax.parser;

import hapax.TemplateLoader;

import java.util.List;

/**
 * Interface that must be implemented by any template parsers.
 *
 * @author dcoker
 */
public interface TemplateParser {

    public List<TemplateNode> parse(TemplateLoader context, String template) 
        throws TemplateParserException;

}
