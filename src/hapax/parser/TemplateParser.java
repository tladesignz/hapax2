package hapax.parser;

import hapax.TemplateLoaderContext;

import java.util.List;

/**
 * Interface that must be implemented by any template parsers.
 *
 * @author dcoker
 */
public interface TemplateParser {

    public List<TemplateNode> parse(TemplateLoaderContext context, String template) 
        throws TemplateParserException;

}
