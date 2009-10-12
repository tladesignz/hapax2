package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateLoader;

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


    TextNode(int lno, String text) {
        super(lno);
        this.text = text;
    }


    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoader context,
                         PrintWriter collector)
    {
        collector.write(text);
    }
}
