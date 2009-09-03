package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;
import java.util.List;

/**
 * Node for the [define] tag.
 *
 * @author dcoker
 * @author jdp
 */
public final class EztDefineNode extends TemplateNode {


    private final String variableName_;


    EztDefineNode(String s) {
        this.variableName_ = s;
    }


    public final TemplateType getTemplateType(){
        return TemplateType.TemplateTypeEztDefine;
    }
    public String getVariableName() {
        return variableName_;
    }

    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter collector) 
        throws TemplateException 
    {
        // do nothing
    }

    public Range advise(List<TemplateNode> nodes, int define_node_idx)
        throws TemplateException
    {
        int other_sections = 0;
        int end_node = -1;

        for (int i = define_node_idx; i < nodes.size(); i++) {
            final TemplateNode node = nodes.get(i);
            if (node instanceof EztDefineNode) {
                other_sections++;
            } else if (node instanceof EztEndNode) {
                other_sections--;
                if (other_sections == 0) {
                    end_node = i;
                    break; // we can stop now because we've found the matching [end]
                }
            }
        }

        if (end_node == -1) {
            throw new TemplateException("Unable to find matching [end] node.");
        }

        return new Range(define_node_idx + 1, end_node, end_node);
    }
}
