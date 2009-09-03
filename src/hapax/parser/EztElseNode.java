package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Node for the [else] tag.
 *
 * @author dcoker
 */
public final class EztElseNode extends TemplateNode {


    EztElseNode() {
        super();
    }

    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                             PrintWriter collector) throws TemplateException
    {
        // do nothing
    }

}
