package hapax.parser;

import hapax.Modifiers;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;
import java.util.List;

/**
 * Represents a node whose output is defined by a value from the
 * TemplateDictionary.
 *
 * This supports both {{PLAIN}} variables as well as one with {{MODIFERS:j}}.
 * The modifiers themselves are implemented in {@link Modifiers}.
 *
 * @author dcoker
 * @author jdp
 */
public final class VariableNode
    extends TemplateNode
{

    private final String variable;

    private final List<Modifiers.FLAGS> modifiers;


    VariableNode(int lno, String spec) {
        this(lno, spec.split(":"));
    }
    private VariableNode(int lno, String[] spec) {
        this(lno,spec[0],Modifiers.parseModifiers(spec));
    }
    private VariableNode(int lno, String variable, List<Modifiers.FLAGS> modifiers) {
        super(lno);
        this.variable = variable;
        this.modifiers = modifiers;
    }


    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter out)
    {
        String t = dict.getVariable(variable);
        if (null == t)
            return;
        else if (this.modifiers.isEmpty())
            out.write(t);
        else {
            t = Modifiers.applyModifiers(t, this.modifiers);
            out.write(t);
        }
    }

}
