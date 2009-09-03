package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Represents a literal string.
 *
 * @author dcoker
 */
public final class TextNode
    extends TemplateNode
{

    private final String text;


    TextNode(String text) {
        super();
        this.text = text;
    }


    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter collector)
    {
        collector.write(text);
    }
}
