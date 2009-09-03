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


    VariableNode(String spec) {
        this(spec.split(":"));
    }
    private VariableNode(String[] spec) {
        this(spec[0],Modifiers.parseModifiers(spec));
    }
    private VariableNode(String variable, List<Modifiers.FLAGS> modifiers) {
        super();
        this.variable = variable;
        this.modifiers = modifiers;
    }


    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter out)
    {
        String t = dict.get(variable);
        if (null == t)
            return;
        else {
            try {
                t = CTemplateParser.Eval(context,dict,t);
            }
            catch (TemplateException ignore){
                ignore.printStackTrace();
            }

            t = Modifiers.applyModifiers(t, this.modifiers);

            out.write(t);
        }
    }

}
