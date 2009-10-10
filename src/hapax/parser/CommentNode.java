package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Represents a ctemplate language comment.
 *
 * @author jdp
 */
public final class CommentNode
    extends TemplateNode
{

    private final String comment;


    CommentNode(int lno, String comment) {
        super(lno);
        this.comment = comment;
    }


    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter collector)
    {
    }
}
